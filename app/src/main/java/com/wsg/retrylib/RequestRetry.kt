package com.retail.newretail.vmc

import com.wsg.retrylib.*
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation


/**
 * 请求重试类
 */
class RequestRetry private constructor() {
    val queue = ArrayBlockingQueue<Any>(10)
    var retryTimes: Long = 10000L
    var retryHashMap: HashMap<String, KFunction<*>> = HashMap()
    val isTerminate: Boolean = true
    var kClassInstance: Any? = null
    /**
     * 上传类型类
     */
    var kClass: KClass<*>? = null


    /**
     * 添加到队列里
     */
    inline fun <reified T> addRequest(t: T, kClassBean: KClass<out Any>, kFunction: KFunction<*>? = null): Boolean {
        val key: String = if (kFunction == null) {

            kClassBean.simpleName.toString()
        } else {
            "${kClassBean.simpleName.toString()}||${kFunction.name}"
        }
        if (retryHashMap.containsKey(key)) {
            val method = retryHashMap[key]
            this.queue.put(RetryBean(t, method!!))
            return true
        }
        return false
    }

    private fun getClassBeanFunc(annotation: List<Annotation>): Boolean {
        val listBoolean: MutableList<Boolean> = mutableListOf()
        for (an in annotation) {
            listBoolean.add(an is ClassBean)
        }
        return listBoolean.contains(true)
    }

    fun configInit() {
        val annotation = kClass!!.java.getAnnotation(UploadClass::class.java)
        kClassInstance = kClass!!.java.newInstance()
        if (annotation != null) {
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
                        "${(annotation as ClassBean).name.simpleName}||${k.name}"
                    } else {
                        if (isRepetition) {
                            throw IllegalArgumentException("No add ClassBean annotation")
                        }
                        (annotation as ClassBean).name.simpleName
                    }
                    retryHashMap[key!!] = k
                }

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

fun main() {
    with(RequestRetry.instance) {
        this.kClass = UpdateClass::class
        this.retryTimes = 5000L
        this.configInit()
        for ((key, value) in RequestRetry.instance.retryHashMap) {
            println("$key===$value")
        }
        val b = this.addRequest(MachineInfo(HashMap()), MachineInfo::class, UpdateClass::uploadMachineInfo2)
        val s = this.addRequest("ss0", String::class)
    }
    Thread(LooperRetryRequestRunnable()).start()
    Thread.sleep(10000)
    RequestRetry.instance.addRequest(MachineInfo(hashMapOf("ad" to "ada")), MachineInfo::class)

}