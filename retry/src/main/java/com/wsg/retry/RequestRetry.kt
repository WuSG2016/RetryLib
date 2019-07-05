package com.wsg.retry


import android.content.Context
import java.lang.IllegalArgumentException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import android.content.IntentFilter
import android.util.Log
import javax.security.auth.login.LoginException


/**
 * 请求重试类
 */

class RequestRetry private constructor() : INetworkListener {
    /**
     * 网络状态变更的回调
     */
    override fun onNetworkState(state: Int) {
        when (state) {
            NetworkBroadcastReceiver.NETWORK_NONE -> {
                Log.e("onNetworkState", "网络已断开")
                stop()
            }
            else -> {
                Log.e("onNetworkState", "网络已连接")
                start()
            }
        }

    }

    private val queue = ArrayBlockingQueue<Any>(20)
    private val retryHashMap: HashMap<String, KFunction<*>> = HashMap()
    var isTerminate: Boolean = true
    var sleepTime: Long = 5000L
    private val poolExecutor = Executors.newFixedThreadPool(10)


    /**
     * 重试次数
     */
    var retryTime: Int = 3
        set(value) {
            if (value <= 1)
                throw IllegalArgumentException("retryTime should bigger than 1")
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
        val filter = IntentFilter()
        filter.addAction(NetworkBroadcastReceiver.NETWORK_ACTION)
        networkBroadcastReceiver = NetworkBroadcastReceiver()
        NetworkBroadcastReceiver.listener = this
        this.isTerminate = NetworkBroadcastReceiver.getNetworkState(context) == NetworkBroadcastReceiver.NETWORK_NONE
        Log.e("Retry", "当前状态网络状态 ${!isTerminate}")
        context.registerReceiver(networkBroadcastReceiver, filter)
    }

    /**
     * 网络广播解除注册
     */
    fun unregisterNetworkReceiver(context: Context) {
        if (networkBroadcastReceiver != null) {
            context.unregisterReceiver(networkBroadcastReceiver)
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
            val method = retryHashMap[key]
            this.queue.put(RetryBean(t, method!!))
            return true
        }
        return false
    }

    fun addRetryBean(retryBean: RetryBean<*>) {
        Log.e("Retry", "继续添加到队列中")
        this.queue.offer(retryBean)
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
                val classBeanFunctionList = memberFunList.filter { getClassBeanFunc(it.annotations) }
                //获取注解里的值
                for (k in classBeanFunctionList) {
                    val annotations = k.annotations
                    var key: String?
                    var isClassBean = false
                    var isRepetition = false
                    var annotation: Annotation? = null
                    for (an in annotations) {
                        if (an is ClassBean) {
                            isClassBean = true
                        } else if (an is Repetition) {
                            isRepetition = true
                        }
                        annotation = an
                    }
                    key = if (isClassBean && isRepetition) {
                        "${(annotation as ClassBean).BeanClass}||${k.name}"
                    } else {
                        if (isRepetition) {
                            throw IllegalArgumentException("No add ClassBean annotation")
                        }
                        (annotation as ClassBean).BeanClass
                    }
                    retryHashMap[key] = k
                }
                poolExecutor.submit(LooperRetryRequestRunnable())
            }
        }
    }

    /**
     * 取出元素
     */
    fun putRequest() = queue.take()!!


    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            RequestRetry()
        }
    }
}

