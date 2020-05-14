package com.wsg.retry

import com.wsg.annotation.ULog
import com.wsg.common.AbstractLogConfig
import com.wsg.common.AbstractLogger
import com.wsg.common.DefaultLogConfig
import com.wsg.common.utils.FileZipUtils

/**
 *  @author WuSG
 *  @date : 2020-05-13 18:27
 */
@ULog(tagName = ["retry"])
class RetryLogger : AbstractLogger() {
    override fun getAbstractLogConfig(): AbstractLogConfig {
        return DefaultLogConfig()
    }

    override fun isRetention(): Boolean {
        return true
    }

    override fun onDetectedFolderPath(): String? {
        return FileZipUtils.generateDefaultLogDirectory("ULog")
    }

    override fun onRetentionTime(): Long {
        return day * 30
    }

}