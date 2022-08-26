package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.HookKey
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.utils.generateId

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

class MemoData<T>(val dependencies: Array<*>, val value: T)