[![Release](https://jitpack.io/v/WuSG2016/RetryLib.svg)](https://jitpack.io/#WuSG2016/RetryLib)
# RetryLib 
# 基于注解+反射的网络重试请求库
##### 使用语言Kotlin
#### Gradle使用方式
- **根目录build.gradle添加** 
```
	allprojects {
		repositories {
			...
			//添加
			maven { url 'https://www.jitpack.io' }
		}
	}
```
- **APP的build.gradle文件增加**
```
dependencies {
	        implementation 'com.github.WuSG2016:RetryLib:Tag'
	}
```
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
```
  val instance = RequestRetry.instance
  instance.addRequest<Any>("dda1","uploadMachineState")
```
#### 3.注解 UploadClass： 需要在上传类上添加,否则无法识别

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
默认使用DefaultRetryIfException类,当遇到SocketTimeoutException,
SocketException, ConnectException异常进行重试,到达最大次数不进行重试。 
### 库的局限性
- **1.**
  重试的请求方法不能及时执行,只能做类似订单上报等不关心上传时机和实时返回的数据等
- **2.** Kotlin第一次反射效率低 之后会比第一次快(但效率略低于JAVA) 
- **3.** UploadClass注解类不能使用private修饰 包括构造方法
### 使用Java的方法构造 比如：
```
     RequestRetry.Companion.getInstance()
                        .setUploadClass()
                        .setRetryIfException()
                        .registerNetworkReceiver(context)
                        .setRetryTime(3)
                        .setSleepTime(1000);
```
###更新
- **Version -1.4**
- 队列包含同一个RetryBean对象时不再添加 RetryBean<*>类型
-   data 数据类      ==   比较参数的内容是否相等
-  class 类        ==   比较的是对象地址
- **Version -1.5**
- 增加日志记录 方便查询
- **Version -1.61**
- 修改异步问题 先记录插入数据再取数据
- 增加设置方式




