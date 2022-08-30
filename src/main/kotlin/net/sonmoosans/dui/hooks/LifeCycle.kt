package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.utils.createKey

/**
 * Run lambda When dependencies updated or initial render
 *
 * @param id ID of hook. If null, generate the ID from lambda
 */
fun RenderContext<*, *>.useEffect(vararg dependencies: Any?, id: String? = null, handle: () -> Unit) {
    val key = createKey(id, handle, "useEffect")

    val cache = data.hooks[key] as Array<*>?
    val updated = cache == null || !cache.contentEquals(dependencies)

    if (updated) {
        handle()

        data.hooks[key] = dependencies
    }
}

/**
 * Run lambda If dependencies updated
 *
 * @param id ID of hook. If null, generate the ID from lambda
 */
fun RenderContext<*, *>.useChange(vararg dependencies: Any?, id: String? = null, handle: () -> Unit) {
    val key = createKey(id, handle, "useChange")
    val cache = data.hooks[key] as Array<*>?

    val updated = cache != null && !cache.contentEquals(dependencies)

    if (updated) handle()
    if (cache == null || updated) {
        data.hooks[key] = dependencies
    }
}

/**
 * @param id ID of hook. If null, generate the ID from lambda
 */
fun<T> RenderContext<*, *>.useMemo(vararg dependencies: Any?, id: String? = null, func: () -> T): T {
    val key = createKey(id, func, "useMemo")

    val cache = data.hooks[key] as MemoData<T>?
    val updated = cache == null || !cache.dependencies.contentEquals(dependencies)

    return if (updated) {
        func().also {
            data.hooks[key] = MemoData(dependencies, it)
        }
    } else cache!!.value
}

class MemoData<T>(val dependencies: Array<*>, val value: T)