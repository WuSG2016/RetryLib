package com.wsg.retrylib

import kotlin.reflect.KClass

/**
 * 上传的类名
 */
@Target(AnnotationTarget.CLASS)
annotation class UploadClass

/**
 * 上传对应的Bean
 */
@Target(AnnotationTarget.FUNCTION)
annotation class ClassBean(val name: KClass<out Any>)

/**
 * 用于添加ClassBean重复的方法
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Repetition