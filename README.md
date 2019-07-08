# RetryLib 
# 基于注解+反射的网络重试请求库
##### 使用语言Kotlin
#### 使用方式
**Gradle方式:添加--到APP的build.gradle文件中**
#### 原理
 库中维护一个阻塞队列,用于添加请求的实体Bean(已封装成RetryBean),同时维护一个线程,
不断的从队列中取出消息,并执行反射中的方法.首先,你需要添加一个上传接口的实现类,
比如：你可以使用JAVA代码编写自己的请求类:
```
@UploadClass
public class ClassA {
    @ClassBean(BeanClass = "MachineInfo")
    public void uploadMachineInfo(RetryBean<MachineInfo> retryBean) {
        System.out.println(retryBean.getT().toString() + "上传机器消息");
    }

    @ClassBean(BeanClass = "String")
    public Integer uploadMachine(RetryBean<String> retryBean) throws Exception {
        if (retryBean.getT().equals("dda3")) {
            return 1;
        }
        if (retryBean.getT().startsWith("dd")) {
            throw new SocketTimeoutException("dd");
        }
        Log.e("uploadMachine: ", "上传机器状态" + retryBean.getT());
        return -1;
    }
    @Repetition
    @ClassBean(BeanClass = "String")
    public void uploadMachineState(RetryBean<String> retryBean) {
        System.out.println(retryBean.getT().toString() + "上传机器状态");
    }
}
```
#### 注解说明
#### 1.注解 ClassBean： RetryBean里传递的类型
#### 2.注解 Repetition： ClassBean注解里如果类型重复，需要添加此注解，同时还需要在添加队列时增加方法名称,比如
#### 1.注解 UploadClass： 上传类需要在类上添加,否则无法识别
```
  val instance = RequestRetry.instance
  instance.addRequest<Any>("dda1","uploadMachineState")
```
#### 注意事项
- **1.** 网络断开状态时 队列停止 不从队列中取出消息,网络连接时队列正常运行
- **2.**
  可拓展的异常重试,如果需要根据自定义类型进行重试,需要重写RetryIfException类,比如
```
/**
 * 增加异常重试类型
 */
open class RetryIfException<T : Exception, E : Any> {
    /**
     * 重试条件
     * e为方法的返回值
     * true 进行重试
     */
    open fun <T, E> onRetryException(retryBean: RetryBean<*>, t: T?, result: E?): Boolean {
        if (retryBean.retryCount >= RequestRetry.instance.retryTime) {
            return false
        }
        return true
    }
}
```
result为方法执行的结果,t为异常类型,可以根据结果和异常类型进行重试
默认使用DefaultRetryIfException类,遇到SocketTimeoutException,
SocketException, ConnectException异常进行重试,到达最大次数不进行重试。 
### 库的局限性
- **1.**
  重试的请求方法不能及时执行,只能做类似订单上报等不关心上传时机和实时返回的数据等
- **2.** Kotlin第一次反射效率低 之后会比第一次快(但效率略低于JAVA) 
### 推荐使用Kotlin的方法构造 比如：
```
   with(RequestRetry.instance) {
        //设置上传类
        this.setUploadClass(UpdateClass::class.java)
        //注册网络监听的广播
        this.registerNetworkReceiver(context)
        this.retryTime = 5
        this.sleepTime = 3L
    }
```