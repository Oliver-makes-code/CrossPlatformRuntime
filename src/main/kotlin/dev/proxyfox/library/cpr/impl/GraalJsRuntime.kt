package dev.proxyfox.library.cpr.impl

import dev.proxyfox.library.cpr.api.CprCallableGuest
import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime
import dev.proxyfox.library.cpr.impl.graal.GraalRunnable
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Value

class GraalJsRuntime : LanguageRuntime {
    val context = Context
        .newBuilder("js")
        .allowHostAccess(HostAccess.EXPLICIT)
        .build()
    val bindings = context.getBindings("js")
    lateinit var value: Value
    lateinit var defaultRunnables: Array<String>
    override fun init(program: String) {
        value = context.parse("js", program)
        value.execute()
    }
    override fun <T> addRunnable(name: String, runnable: CprCallableHost<T>) {
        bindings.putMember("__cpr_internal_${name.replace(".","_")}__", GraalRunnable(this, runnable))
        context.eval("js", """
            function ${name.replace(".","_")}() {
                return __cpr_internal_${name.replace(".","_")}__.run(arguments)
            }
        """.trimIndent())
    }
    override fun run() {
        getRunnable("main")()
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