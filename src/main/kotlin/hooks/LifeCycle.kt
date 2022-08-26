package hooks

import HookKey
import context.RenderContext

fun RenderContext<*, *>.useEffect(vararg dependencies: Any?, id: String? = null, handle: () -> Unit) {
    val key = HookKey(generateId(id, handle), "useEffect")

    val cache = data.hooks[key] as Array<*>?
    val updated = cache == null || !cache.contentEquals(dependencies)

    if (updated) {
        handle()

        data.hooks[key] = dependencies
    }
}

fun<T> RenderContext<*, *>.useMemo(vararg dependencies: Any?, id: String? = null, func: () -> T): T {
    val key = HookKey(generateId(id, func), "useMemo")

    val cache = data.hooks[key] as MemoData<T>?
    val updated = cache == null || !cache.dependencies.contentEquals(dependencies)

    return if (updated) {
        func().also {
            data.hooks[key] = MemoData(dependencies, it)
        }
    } else cache!!.value
}

/**
 * In default, we will use the hashcode of the interface class
 */
private fun generateId(id: String?, func: () -> Any?): String {
    return id?: func::class.hashCode().toString()
}

class MemoData<T>(val dependencies: Array<*>, val value: T)