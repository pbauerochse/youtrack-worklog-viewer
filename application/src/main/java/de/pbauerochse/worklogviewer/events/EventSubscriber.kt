package de.pbauerochse.worklogviewer.events

import org.slf4j.LoggerFactory
import java.lang.reflect.Method

internal class EventSubscriber(
    internal val method: Method,
    private val subscriber: Any
) {
    val eventClass: Class<*> = method.parameterTypes.first()
    val genericEntityParameterClass = method.getDeclaredAnnotation(Subscribe::class.java).genericType

    fun handleEvent(event: Any) {
        LOGGER.debug("Invoking $method with Parameter $event")
        method.invoke(subscriber, event)
    }

    override fun toString(): String {
        return "EventSubscriber(method=$method)"
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EventSubscriber::class.java)
    }
}