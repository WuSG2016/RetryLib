package com.wsg.retrylib;

import android.util.Log;
import com.wsg.retry.ClassBean;
import com.wsg.retry.Repetition;
import com.wsg.retry.RetryBean;
import com.wsg.retry.UploadClass;

import java.net.SocketTimeoutException;

@UploadClass
public class ClassA {
    @ClassBean(BeanClass = "MachineInfo")
    public void uploadmachineInfo(RetryBean<MachineInfo> retryBean) {
        System.out.println(retryBean.getT().toString() + "上传机器消息");
    }

    @ClassBean(BeanClass = "String")
    public void uploadmachine(RetryBean<String> retryBean) throws SocketTimeoutException {
        if(retryBean.getT().equals("dda4")){
            throw new SocketTimeoutException("dd");
        }
        Log.e("uploadmachine: ", "上传机器状态" + retryBean.getT());
    }
}
