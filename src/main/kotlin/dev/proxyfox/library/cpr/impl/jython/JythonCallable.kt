package dev.proxyfox.library.cpr.impl.jython

import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime

class JythonCallable(val context: LanguageRuntime, val runner: CprCallableHost) {
    fun run(params: Array<Any?>): Any? {
        return runner(context, params)
    }
}