package dev.proxyfox.library.cpr.api

typealias CprCallableHost<T> = LanguageRuntime.(args: Array<Any?>) -> T

class CprCallableGuest(val runner: (args: Array<out Any?>) -> Any?) {
    operator fun invoke(vararg args: Any?): Any? {
        return runner(args)
    }
}

interface LanguageRuntime {
    fun init(program: String)
    fun <T> addRunnable(name: String, runnable: CprCallableHost<T>)
    fun run()
    fun getRunnables(): Array<String>
    fun getRunnable(name: String): CprCallableGuest
}