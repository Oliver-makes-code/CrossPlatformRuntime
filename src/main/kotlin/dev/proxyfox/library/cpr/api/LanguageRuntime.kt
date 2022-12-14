package dev.proxyfox.library.cpr.api

typealias CprCallableHost = LanguageRuntime.(args: Array<Any?>) -> Any?

class CprCallableGuest(val runner: (args: Array<out Any?>) -> Any?) {
    operator fun invoke(vararg args: Any?): Any? {
        return runner(args)
    }
}

interface LanguageRuntime {
    val langPrefix: String
    var defaultRunnables: Array<String>
    fun init(program: String)
    fun addRunnable(name: String, runnable: CprCallableHost)
    fun run() {
        getRunnable("main")()
    }
    fun getRunnables(): Array<String>
    fun getRunnable(name: String): CprCallableGuest
    fun exportDefaultRunnables(other: LanguageRuntime) {
        for (name in defaultRunnables) {
            other.addRunnable("${langPrefix}_$name") {
                this@LanguageRuntime.getRunnable(name)()
            }
        }
    }
}