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
        Log.e("uploadmachine: ", "上传机器状态" + retryBean.getT());
        return -1;
    }

    @Repetition
    @ClassBean(BeanClass = "String")
    public void uploadMachineState(RetryBean<String> retryBean) {
        System.out.println(retryBean.getT().toString() + "上传机器状态");
    }
}
