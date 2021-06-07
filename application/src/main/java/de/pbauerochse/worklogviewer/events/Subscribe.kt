package de.pbauerochse.worklogviewer.events

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Subscribe(
    val genericType: KClass<*> = Unit::class
)
