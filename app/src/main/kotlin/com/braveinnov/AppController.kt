package com.braveinnov

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@TransitionalService(namespace = "search")
class AppController {

    @TransitionalTypes(namespace = "search")
    fun definedTypes(): Map<String, Any> {
        println("Invoking...")
        return mapOf<String, Any>("Hello" to mapOf<String, Any>( "string" to listOf<String>("name", "description")))
    }

    @GetMapping(name = "/hello")
    fun sayHi(request: Request): Response {
        listOf<String>()
        return Response(200, "Success")
    }
}

class Request (val name: String){}
class Response(val status: Int, val message: String) {}