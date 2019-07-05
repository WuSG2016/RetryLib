package com.wsg.retrylib;

import android.net.Network;
import com.wsg.retry.LooperRetryRequestRunnable;
import com.wsg.retry.RequestRetry;
import kotlin.reflect.KClass;
import kotlin.reflect.KFunction;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {
        RequestRetry instance = RequestRetry.Companion.getInstance();
        instance.setUploadClass(ClassA.class);
        boolean isok=instance.addRequest(new MachineInfo(new HashMap(2)));
        System.out.println(isok);

    }
}
