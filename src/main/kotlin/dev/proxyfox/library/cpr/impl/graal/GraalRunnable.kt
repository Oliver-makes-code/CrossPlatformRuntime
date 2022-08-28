package dev.proxyfox.library.cpr.impl.graal

import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime
import org.graalvm.polyglot.HostAccess.Export

data class GraalRunnable(val context: LanguageRuntime, val runner: CprCallableHost) {
    @Export
    fun run(params: Array<Any?>): Any? {
        return runner(context, params)
    }
}