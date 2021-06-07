package de.pbauerochse.worklogviewer.events

import org.slf4j.LoggerFactory
import java.lang.reflect.Method

/**
 * A basic EventBus for publishing ans subscribing to Events
 */
object EventBus {

    private val logger = LoggerFactory.getLogger(EventBus::class.java)
    private val subscribers = mutableListOf<EventSubscriber>()

    /**
     *
     */
    fun publish(event: Any) {
        val eventClass = event.javaClass
        subscribers
            .filter { it.eventClass.isAssignableFrom(eventClass) }
            .forEach { eventSubscriber ->
                logger.info("Invoking Subscriber $event for Event $event")
                eventSubscriber.handleEvent(event)
            }
    }

    fun subscribe(subscriber: Any) {
        getAllMethods(subscriber)
            .filter { it.isAnnotationPresent(Subscribe::class.java) }
            .filter {
                check(it.parameterCount == 1) { "Subscribe methods may only have one parameter. Method ${it.name} in ${subscriber.javaClass.name} has ${it.parameterCount} parameters" }
                it.parameterCount == 1
            }
            .map { EventSubscriber(it, subscriber) }
            .onEach { logger.info("Registering Subscriber ${subscriber.javaClass.name}.${it.method.name}") }
            .forEach { subscribers.add(it) }
    }

    private fun getAllMethods(subscriber: Any): List<Method> {
        val subscriberClass = subscriber.javaClass
        val methods = mutableListOf<Method>(*subscriberClass.declaredMethods)
        if (subscriberClass != Any::class.java && subscriberClass != Class::class.java) {
            methods.addAll(getAllMethods(subscriberClass.superclass))
        }
        return methods
    }

}