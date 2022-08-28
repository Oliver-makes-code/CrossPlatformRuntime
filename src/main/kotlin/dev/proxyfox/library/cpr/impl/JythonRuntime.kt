package dev.proxyfox.library.cpr.impl

import dev.proxyfox.library.cpr.api.CprCallableGuest
import dev.proxyfox.library.cpr.api.CprCallableHost
import dev.proxyfox.library.cpr.api.LanguageRuntime
import dev.proxyfox.library.cpr.impl.jython.JythonCallable
import org.python.core.Py
import org.python.core.PyCode
import org.python.core.PyObject
import org.python.core.PyStringMap
import org.python.core.PySystemState
import org.python.util.PythonInterpreter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class JythonRuntime : LanguageRuntime {
    private val python = PythonInterpreter()
    private lateinit var code: PyCode
    override val langPrefix: String = "py"
    override lateinit var defaultRunnables: Array<String>

    override fun init(program: String) {
        code = python.compile(program)
        python.eval(code)
        defaultRunnables = getRunnables()
    }

    override fun addRunnable(name: String, runnable: CprCallableHost) {
        python["__cpr_internal_${name.replace(".","_")}__"] = JythonCallable(this, runnable)
        python.exec("""
            def ${name.replace(".","_")}(*args):
                return __cpr_internal_${name.replace(".","_")}__.run(args)
        """.replace("\n            ","\n"))
    }

    override fun run() {
        python["main"].__call__()
    }

    override fun getRunnables(): Array<String> {
        val out = ArrayList<String>()
        val locals = python.locals as PyStringMap
        for (name in locals.map.keys) {
            if (name is String && locals.map[name]!!.isCallable) {
                out.add(name)
            }
        }
        return out.toTypedArray()
    }

    override fun getRunnable(name: String): CprCallableGuest {
        return CprCallableGuest {
            if (it.isEmpty()) return@CprCallableGuest python[name].__call__().toJavaObject()
            python[name].__call__(it.toPyObjects()).toJavaObject()
        }
    }

    companion object {
        init {
            val properties = Properties()
            properties["python.import.site"] = "false"
            PySystemState.initialize(System.getProperties(), properties)
        }
    }
}

private fun <T> T.toPyObject(): PyObject {
    return Py.java2py(this)
}
private fun <T> Array<T>.toPyObjects(): Array<out PyObject> {
    return Py.javas2pys(*this)
}
private fun PyObject.toJavaObject(): Any? {
    return Py.tojava(this, JvmType.Object::class.java)
}