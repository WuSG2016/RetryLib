package com.wsg.retry

/**
 * 上传的类名
 */
@Target(AnnotationTarget.CLASS)
annotation class UploadClass

/**
 * 上传对应的Bean
 */
@Target(AnnotationTarget.FUNCTION)
annotation class ClassBean(val BeanClass: String)

/**
 * 用于添加ClassBean重复的方法
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Repetition