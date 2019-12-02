package com.wsg.retry


import android.content.BroadcastReceiver
import android.content.Context
import java.lang.IllegalArgumentException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import android.content.IntentFilter
import android.util.Log
import com.wsg.common.Logger
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap


/**
 * 请求重试类
 */

class RequestRetry private constructor() : INetworkListener {
    private val logTag = "retryLog"
    /**
     * 网络状态变更的回调
     */
    override fun onNetworkState(state: Int) {
        when (state) {
            NetworkBroadcastReceiver.NETWORK_NONE -> {
                Logger.otherTagLog("Retry", "网络已断开", logTag)
                stop()
            }
            else -> {
                Logger.otherTagLog("Retry", "网络已连接", logTag)
                start()
            }
        }

    }

    private val queue = ArrayBlockingQueue<Any>(20)
    private val retryHashMap: HashMap<String, KFunction<*>> = HashMap()
    var isTerminate: Boolean = true
    var sleepTime: Long = 5000L
    private val poolExecutor = Executors.newFixedThreadPool(10)
    private var weakReference: WeakReference<Context>? = null


    /**
     * 重试次数
     */
    var retryTime: Int = 3
        set(value) {
            require(value > 1) { "retryTime should bigger than 1" }
            field = value
        }

    var kClassInstance: Any? = null
    private var networkBroadcastReceiver: NetworkBroadcastReceiver? = null
    /**
     * 默认重试异常
     */
    var retryIfException: RetryIfException<*, *> = DefaultRetryIfException()

    /**
     * 上传类型类
     */
    private var kClass: KClass<*>? = null
    private val mRecordMap: ConcurrentHashMap<RetryBean<*>, Any?> = ConcurrentHashMap()

    fun setUploadClass(t: Class<*>) {
        kClass = t.kotlin
        configInit()
    }

    fun <T> addRequest(t: T): Boolean {
        return this.addRequest(t, "")
    }

    /**
     * 网络广播注册
     */
    fun registerNetworkReceiver(context: Context) {
        weakReference = WeakReference(context)
        val filter = IntentFilter()
        filter.addAction(NetworkBroadcastReceiver.NETWORK_ACTION)
        networkBroadcastReceiver = NetworkBroadcastReceiver()
        NetworkBroadcastReceiver.listener = this
        this.isTerminate =
            NetworkBroadcastReceiver.getNetworkState(weakReference?.get()) == NetworkBroadcastReceiver.NETWORK_NONE
        Logger.otherTagLog("Retry", "当前状态网络状态 ${!isTerminate}", logTag)
        weakReference?.get()?.registerReceiver(networkBroadcastReceiver, filter)
    }

    /**
     * 网络广播解除注册
     */
    fun unregisterNetworkReceiver() {
        if (networkBroadcastReceiver != null) {
            weakReference?.get()?.unregisterReceiver(networkBroadcastReceiver)
            networkBroadcastReceiver = null
        }
    }

    /**
     * 添加到队列里
     */
    fun <T> addRequest(t: T, method: String = ""): Boolean {
        val al = t as Any?
        val key: String = if (method.isEmpty()) {
            al!!.javaClass.simpleName
        } else {
            "${al!!.javaClass.simpleName}||$method"
        }
        if (retryHashMap.containsKey(key)) {
            val function = retryHashMap[key]
            val retryBean = RetryBean(t, function!!)
            //判断是否已经添加过
            return if (!mRecordMap.containsKey(retryBean)) {
                this.queue.offer(retryBean)
                mRecordMap[retryBean] = function
                Logger.otherTagLog("添加retryBean到队列->>", retryBean.toString(), logTag)
                true
            } else {
                Logger.otherTagLog("队列已存在记录->>", "${retryBean}不添加", logTag)
                false
            }

        }
        return false
    }

    /**
     * 判断队列是否包含
     */
    fun addRetryBean(retryBean: RetryBean<*>) {
        Logger.otherTagLog("addRetryBean", "重新添加到队列中$retryBean", logTag)
        if (!mRecordMap.containsKey(retryBean)) {
            this.queue.offer(retryBean)
            this.mRecordMap[retryBean] = retryBean.kFunction
            Logger.otherTagLog("添加-->", retryBean.toString(), logTag)
        } else {
            Logger.otherTagLog("队列已存在记录->>", retryBean.toString(), logTag)
        }
    }

    private fun getClassBeanFunc(annotation: List<Annotation>): Boolean {
        val listBoolean: MutableList<Boolean> = mutableListOf()
        for (an in annotation) {
            listBoolean.add(an is ClassBean)
        }
        return listBoolean.contains(true)
    }


    private fun stop() {
        this.isTerminate = true
    }

    private fun start() {
        this.isTerminate = false
    }

    private fun configInit() {
        val annotation = kClass!!.java.getAnnotation(UploadClass::class.java)
        if (annotation != null) {
            kClassInstance = kClass!!.java.newInstance()
            val memberFunList: Collection<KFunction<*>> = kClass!!.declaredMemberFunctions
            if (memberFunList.isNotEmpty()) {
                //获取带有ClassBean注解的方法
                val classBeanFunctionList =
                    memberFunList.filter { getClassBeanFunc(it.annotations) }
                //获取注解里的值
                for (k in classBeanFunctionList) {
                    val annotations = k.annotations
                    var key: String?
                    var isClassBean = false
                    var isRepetition = false
                    var annotationClassBean: Annotation? = null
                    for (an in annotations) {
                        if (an is ClassBean) {
                            isClassBean = true
                            annotationClassBean = an
                        } else if (an is Repetition) {
                            isRepetition = true
                        }
                    }
                    key = if (isClassBean && isRepetition) {
                        "${(annotationClassBean!! as ClassBean).BeanClass}||${k.name}"
                    } else {
                        require(!isRepetition) { "No add ClassBean annotation" }
                        (annotationClassBean!! as ClassBean).BeanClass
                    }
                    retryHashMap[key] = k
                }
                poolExecutor.submit(LooperRetryRequestRunnable())
                initLog()
            }
        }
    }

    private fun initLog() {
        Logger.init()
        Logger.addLogFile(logTag)

    }

    /**
     * 取出元素 删除记录
     */
    fun putRequest(): RetryBean<*>? {
        return when (val retryBean = queue.take()) {
            is RetryBean<*> -> {
                if (mRecordMap.containsKey(retryBean)) {
                    mRecordMap.remove(retryBean)
                    retryBean
                } else {
                    Logger.otherTagLog("数据未存在插入信息队列中", retryBean.toString(), logTag)
                    null
                }
            }
            else -> {
                null
            }
        }
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RequestRetry()
        }
    }
}

