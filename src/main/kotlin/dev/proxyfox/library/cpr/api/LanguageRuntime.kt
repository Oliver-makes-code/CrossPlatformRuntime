package dev.proxyfox.library.cpr.api

typealias CprCallableHost = LanguageRuntime.(args: Array<Any?>) -> Any?

class CprCallableGuest(val runner: (args: Array<out Any?>) -> Any?) {
    operator fun invoke(vararg args: Any?): Any? {
        return runner(args)
    }
}

interface LanguageRuntime {
    fun init(program: String)
    fun addRunnable(name: String, runnable: CprCallableHost)
    fun run()
    fun getRunnables(): Array<String>
    fun getRunnable(name: String): CprCallableGuest
}