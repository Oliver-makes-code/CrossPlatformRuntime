package dev.proxyfox.library.cpr.test

import dev.proxyfox.library.cpr.api.LanguageRuntime
import dev.proxyfox.library.cpr.impl.graal.GraalRunnable
import dev.proxyfox.library.cpr.impl.GraalJsRuntime

fun main() {
    val js = GraalJsRuntime()
    js.init("""
        function test(str) {
            console.log(str)
            console.log("JS function called!")
        }
        testFunction()
    """.trimIndent())
    js.addRunnable("testFunction") {
        println("Kotlin function called!")
        getRunnable("test")("owo")
    }
    js.run()
}