package dev.proxyfox.library.cpr.impl.graal

import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime
import org.graalvm.polyglot.HostAccess.Export

data class GraalRunnable<T>(val context: LanguageRuntime, val runner: CprCallableHost<T>) {
    @Export
    fun run(params: Array<Any?>): T {
        return runner(context, params)
    }
}