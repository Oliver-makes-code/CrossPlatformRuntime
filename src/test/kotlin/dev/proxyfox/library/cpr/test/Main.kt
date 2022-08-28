package dev.proxyfox.library.cpr.test

import dev.proxyfox.library.cpr.api.LanguageRuntime
import dev.proxyfox.library.cpr.impl.graal.GraalRunnable
import dev.proxyfox.library.cpr.impl.GraalJsRuntime
import dev.proxyfox.library.cpr.impl.JythonRuntime

fun main() {
    val js = GraalJsRuntime()
    val py = JythonRuntime()

    js.init("""
        function main() {
            py_test()
        }
        function test() {
            console.log("JavaScript called from Python!")
        }
    """.replace("\n        ","\n"))

    py.init("""
        def main():
            js_test()
        def test():
            print("Python called from JavaScript!")
    """.replace("\n        ","\n"))
    js.addRunnable("py_test") {
        py.getRunnable("test")(*it)
    }
    py.addRunnable("js_test") {
        js.getRunnable("test")(*it)
    }

    js.run()
    py.run()
}