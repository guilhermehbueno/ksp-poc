package com.braveinnov.controller

import com.braveinnov.TransitionalService
import com.braveinnov.TransitionalTypes
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.EnableWebMvc


/**
 *
 */
@Controller
@EnableWebMvc
@TransitionalService(namespace = "search")
class AppController (){

    init {
        println("Loading AppController...")
    }

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

    @GetMapping("/hello")
    fun sayHi(): ResponseEntity<String> {
        return ResponseEntity.ok("Success")
    }
}
class Request (val name: String){}
class Response(val status: Int, val message: String) {}