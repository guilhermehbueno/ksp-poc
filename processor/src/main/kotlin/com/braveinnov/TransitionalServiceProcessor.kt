package com.braveinnov

import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.lang.reflect.Method


class TransitionalServiceProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
): SymbolProcessor {

    private lateinit var file: OutputStream

    private fun emit(s: String?) {
        s?.let {
            file.write("$s\n".toByteArray())
        }
    }

    private var invoked = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        file = codeGenerator.createNewFile(Dependencies(false), "com.braveinnov", "HelloKSP")
        file.write("""
            package com.braveinnov
            class HelloKSP{}
        """.trimIndent().toByteArray())

        val symbols = resolver.getSymbolsWithAnnotation(TransitionalService::class.qualifiedName!!)
        symbols
            .filterIsInstance<KSClassDeclaration>()
            .forEach { classDeclaration ->
                // Access the annotation arguments
                val annotation = classDeclaration.annotations.first { it.shortName.getShortName() == "TransitionalService" }
                val namespace = annotation.arguments.first { it.name?.getShortName() == "namespace" }.value as String

                // Log or use the namespace value as needed
                logger.info("Found @TransitionalService with namespace: $namespace on ${classDeclaration.qualifiedName?.asString()}")
            }

        invoked = true
        file.close()
        return emptyList()
    }

    inner class BuilderVisitor : KSVisitorVoid() {
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {

        }
    }
}


/**
 * See: https://github.com/google/ksp/blob/main/examples/playground/test-processor/src/main/kotlin/BuilderProcessor.kt
 */
