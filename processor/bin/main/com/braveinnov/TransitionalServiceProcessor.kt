package com.braveinnov

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStream
import java.io.OutputStreamWriter

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

    var invoked = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        file = codeGenerator.createNewFile(Dependencies(false), "", "transitional-service-manifest", "yaml")


        val symbols = resolver
            .getSymbolsWithAnnotation("com.braveinnov.TransitionalService")
            .filterIsInstance<KSClassDeclaration>()


        symbols.forEach { symbol ->


            symbol.accept(BuilderVisitor(), OutputStreamWriter(file))
            val annotation: KSAnnotation = symbol.annotations.first{
                it.shortName.asString() == "TransitionalService"
            }

            val namespace: KSValueArgument = annotation.arguments
                .first { arg -> arg.name?.asString() == "namespace" }

            val namespaceValue = namespace.value as String

            emit("Namespace: $namespaceValue")
            emit("Name: ${symbol.simpleName.getShortName()}")
            val functions = symbol.getAllFunctions()
            functions.forEach { function ->
                val getMapping = function.annotations.filter {
                    it.shortName.asString() == "GetMapping"
                }.firstOrNull()

                getMapping?.let { annotation ->
                    emit("getMapping: ${getMapping.toString()}")
                    annotation.arguments.forEach {
                        emit("getMapping arg: ${it.toString()} -> ${it.value}")
                    }
                    emit("SimpleName: ${function.simpleName.asString()}")
                    emit("QualifiedName: ${function.qualifiedName?.getShortName()}")

                    function.parameters.forEach{ it ->
                        emit("parameter: $it - ${it.type.resolve().declaration.qualifiedName}")
                    }
                }
            }
        }

        file.close()
        invoked = true
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
