package com.braveinnov

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@TransitionalService(namespace = "search")
class AppController {

    @GetMapping(name = "/hello")
    fun sayHi(request: Request): Response {
        return Response(200, "Success")
    }
}

class Request (val name: String){}
class Response(val status: Int, val message: String) {}