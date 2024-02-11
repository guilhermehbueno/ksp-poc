package com.braveinnov

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.KClass

/**
 * Class responsible for invoking a class method using Reflection.
 * At the end it should generate files based on the method return.
 *
 * @see com.braveinnov.AppController
 */
object Reflector {
    @JvmStatic
    fun main(args: Array<String>) {
        val className = args[0]
        val methodName = args[1]

        val clazz = Class.forName(className, )
        val method = clazz.getDeclaredMethod(methodName)
        val instance = clazz.getDeclaredConstructor().newInstance()

        val types = method.invoke(instance) as Map<String, Any>

        val outputDir = File("build/generated/tg/main/kotlin/com/example")
        outputDir.mkdirs() // Ensure the directory exists

        val fos = FileOutputStream("build/generated/tg/main/kotlin/com/example/${types.keys.first()}.kt")

        /**
         * We can create the file using the Kotlin Poet.
         * Following is a simple example
         */
        fos.write("""
            package com.example

            class ${types.keys.first()} {
                fun someMethod(): Boolean {
                    return true
                }
            }

        """.trimIndent().toByteArray())

    }
}