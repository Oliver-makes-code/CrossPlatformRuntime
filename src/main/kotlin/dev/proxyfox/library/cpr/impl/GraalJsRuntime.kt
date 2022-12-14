package dev.proxyfox.library.cpr.impl

import dev.proxyfox.library.cpr.api.CprCallableGuest
import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime
import dev.proxyfox.library.cpr.impl.graal.GraalRunnable
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Value

class GraalJsRuntime : LanguageRuntime {
    private val context: Context = Context
        .newBuilder("js")
        .allowHostAccess(HostAccess.EXPLICIT)
        .build()
    private val bindings: Value = context.getBindings("js")
    private lateinit var value: Value
    override lateinit var defaultRunnables: Array<String>
    override val langPrefix: String = "js"

    override fun init(program: String) {
        value = context.parse("js", program)
        value.execute()
        defaultRunnables = getRunnables()
    }
    override fun addRunnable(name: String, runnable: CprCallableHost) {
        bindings.putMember("__cpr_internal_${name.replace(".","_")}__", GraalRunnable(this, runnable))
        context.eval("js", """
            function ${name.replace(".","_")}() {
                return __cpr_internal_${name.replace(".","_")}__.run(arguments)
            }
        """.trimIndent())
    }

    override fun getRunnables(): Array<String> {
        return getRunnables(bindings)
    }

    private fun getRunnables(member: Value, path: String = ""): Array<String> {
        val out = ArrayList<String>()
        val members = member.memberKeys
        for (memberName in members) {
            val value = member.getMember(memberName)
            val newPath = if (path.isBlank()) memberName else "$path.$memberName"
            if (value.canExecute()) {
                out.add(newPath)
            }
            out.addAll(getRunnables(value, newPath))
        }
        return out.toTypedArray()
    }

    override fun getRunnable(name: String): CprCallableGuest {
        val split = name.split(".")
        var member = bindings
        for (i in split) {
            member = member.getMember(i)
        }
        return CprCallableGuest {
            member.execute(*it)
        }
    }

    companion object {
        init {
            System.setProperty("polyglot.engine.WarnInterpreterOnly", "false")
        }
    }
}