package com.wsg.retrylib;

import android.net.Network;

import com.wsg.retry.LooperRetryRequestRunnable;
import com.wsg.retry.RequestRetry;
import com.wsg.retry.RetryBean;

import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        RequestRetry instance =
                RequestRetry.Companion.getInstance()
                        .setUploadClass(null)
                        .setRetryIfException(null)
                        .registerNetworkReceiver(null)
                        .setRetryTime(3)
                        .setSleepTime(1000);




//        instance.setUploadClass(ClassA.class);
//        instance.addRequest(new MachineInfo(new HashMap(2)));
//        instance.addRequest("dda1");
//        instance.addRequest("dda2");
//        instance.addRequest("dda3");
//        instance.addRequest("dda4");
//        instance.addRequest("dda5");
//        instance.addRequest("dda6");

    }
}
