package com.braveinnov

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
@RestController
@TransitionalService(namespace = "search")
class AppController {

    /**
     * @see com.braveinnov.Reflector
     */
    @TransitionalTypes(namespace = "search")
    fun definedTypes(): Map<String, Any> {
        println("""
            === Heads up! ===
            Invoking from Reflector.
            This method will return a map describing a class we'll create.
            The goal is to validate that at compile time we'll Generate new files based on a method invocation in classes in the main project.
             
            At the end, the .class should be inside the generated jar.
            jar -tf app/build/libs/app-plain.jar
        """.trimIndent())
        return mapOf<String, Any>("HelloItShouldBeIncludedInTheFinalJar" to mapOf<String, Any>( "string" to listOf<String>("name", "description")))
    }

    @GetMapping(name = "/hello")
    fun sayHi(request: Request): Response {
        HelloKSP()
        return Response(200, "Success")
    }
}

class Request (val name: String){}
class Response(val status: Int, val message: String) {}