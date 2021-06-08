package de.pbauerochse.worklogviewer.events

import java.lang.reflect.Method

internal class EventSubscriber(
    internal val method: Method,
    private val subscriber: Any
) {
    val eventClass: Class<*> = method.parameterTypes.first()
    val genericEntityParameterClass = method.getDeclaredAnnotation(Subscribe::class.java).genericType

    override fun toString(): String {
        return "EventSubscriber(method=$method, subscriber=$subscriber, eventClass=$eventClass, genericEntityParameterClass=$genericEntityParameterClass)"
    }

    fun handleEvent(event: Any) {
        method.invoke(event)
    }
}