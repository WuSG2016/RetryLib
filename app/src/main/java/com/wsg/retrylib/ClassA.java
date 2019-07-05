package com.wsg.retrylib;

import com.wsg.retry.ClassBean;
import com.wsg.retry.Repetition;
import com.wsg.retry.RetryBean;
import com.wsg.retry.UploadClass;

@UploadClass
public class ClassA {
    @ClassBean(BeanClass = "MachineInfo")
    public void uploadmachineInfo(RetryBean<MachineInfo> retryBean) {
        System.out.println(retryBean.getT().toString() + "上传机器消息");
    }
}
