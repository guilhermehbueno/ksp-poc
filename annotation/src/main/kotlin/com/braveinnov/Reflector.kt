package com.braveinnov

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.io.FileOutputStream

object Reflector {
    @JvmStatic
    fun main(args: Array<String>) {
        val className = args[0]
        val methodName = args[1]

        val clazz = Class.forName(className, )
        val method = clazz.getDeclaredMethod(methodName)
        val instance = clazz.getDeclaredConstructor().newInstance()

        method.invoke(instance)


        // Define the output directory
        val outputDir = File("build/generated/tg/main/kotlin/com/example")
        outputDir.mkdirs() // Ensure the directory exists

        val fos = FileOutputStream("build/generated/tg/main/kotlin/com/example/HelloWorld.kt")
        fos.write("""
            package com.example

            class HelloWorld {
                fun someLibraryMethod(): Boolean {
                    return true
                }
            }

        """.trimIndent().toByteArray())

    }
}