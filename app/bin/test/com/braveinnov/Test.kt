package com.braveinnov

import org.junit.Test
import kotlin.test.assertEquals

class Test {

    @Test
    fun testReflection(){
        Response::class.java.kotlin.members.forEach { field ->
            println("Field: ${field.name}: ${field.returnType}")
        }
    }
}