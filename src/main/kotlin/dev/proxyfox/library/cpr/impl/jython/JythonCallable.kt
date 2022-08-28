package dev.proxyfox.library.cpr.impl.jython

import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime

class JythonCallable<T>(val context: LanguageRuntime, val runner: CprCallableHost<T>) {
    fun run(params: Array<Any?>): T {
        return runner(context, params)
    }
}