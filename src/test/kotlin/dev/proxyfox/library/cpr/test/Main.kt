package dev.proxyfox.library.cpr.test

import dev.proxyfox.library.cpr.impl.GraalJsRuntime
import dev.proxyfox.library.cpr.impl.JythonRuntime

fun main() {
    val js = GraalJsRuntime()
    val py = JythonRuntime()

    js.init("""
        function main() {
            
        }
    """.replace("\n        ","\n"))

    py.init("""
        def main():
            pass
    """.replace("\n        ","\n"))

    js.exportTopLevelRunnables(py)
    py.exportTopLevelRunnables(js)

    js.run()
    py.run()
}