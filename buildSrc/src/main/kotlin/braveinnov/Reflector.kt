package com.braveinnov

import java.io.File


//Not using it
object Reflector {
    /**
     * Invokes a no-argument method specified by [methodName] on a class specified by [className].
     * @param className The fully qualified name of the class.
     * @param methodName The name of the method to invoke.
     * @return The return value of the invoked method.
     */
    fun invokeTargetMethod(className: String, methodName: String): Any? {
        val clazz = Class.forName(className)
        val method = clazz.getDeclaredMethod(methodName)
        method.isAccessible = true // If the method is not public
        val instance = clazz.getDeclaredConstructor().newInstance() // Assuming a no-arg constructor


        return method.invoke(instance)
    }
}