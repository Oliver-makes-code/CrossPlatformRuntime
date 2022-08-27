package dev.proxyfox.library.cpr.impl

import dev.proxyfox.library.cpr.api.CprCallableGuest
import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime
import dev.proxyfox.library.cpr.impl.graal.GraalRunnable
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Value
import kotlin.reflect.KFunction

class GraalJsRuntime : LanguageRuntime {
    val context = Context
        .newBuilder("js")
        .allowHostAccess(HostAccess.EXPLICIT)
        .build()
    val bindings = context.getBindings("js")
    lateinit var value: Value
    override fun init(program: String) {
        value = context.parse("js", program)
    }
    override fun <T> addRunnable(name: String, runnable: CprCallableHost<T>) {
        bindings.putMember("__cpr_internal_${name}__", GraalRunnable(this, runnable))
        context.eval("js", """
            function $name() {
                return __cpr_internal_${name}__.run(arguments)
            }
        """.trimIndent())
    }
    override fun run() {
        value.execute()
    }

    override fun getRunnables(): Array<String> {
        val out = ArrayList<String>()
        val members = bindings.memberKeys
        for (member in members) {
            if (bindings.getMember(member).canExecute()) {
                out.add(member)
            }
        }
        return out.toTypedArray()
    }

    override fun getRunnable(name: String): CprCallableGuest {
        return CprCallableGuest {
            bindings.getMember(name).execute(*it)
        }
    }
}