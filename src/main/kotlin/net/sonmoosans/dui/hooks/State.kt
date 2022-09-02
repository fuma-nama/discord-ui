package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.HookKey
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.utils.Delegate
import net.sonmoosans.dui.utils.createKey

/**
 * Create a State
 *
 * State will be memorized every renders
 *
 * @param id ID of the Hook, Generates an ID from lambda If null
 */
fun<S> RenderContext<*, *>.useState(id: String? = null, initial: () -> S): State<S> {
    val key = createKey(id, initial, "useState")
    if (!data.hooks.containsKey(key)) {
        data.hooks[key] = initial()
    }

    return State(key, data)
}

fun<S> RenderContext<*, *>.useState(id: String? = null, initial: S) = useState(id) { initial }

class State<S>(val key: HookKey, val data: Data<*>): Delegate<S> {
    override var value: S
        get() = data.hooks[key] as S
        set(value) { data.hooks[key] = value }

    fun cached() = Cached(data.hooks[key] as S)

    inner class Cached(var cached: S): Delegate<S> {
        override var value: S
            get() = cached
            set(value) {
                cached = value
                data.hooks[key] = value
            }
    }
}

fun<S> RenderContext<*, *>.useRef(id: String, initial: S) = useRef(id as String?) { initial }

fun<S> RenderContext<*, *>.useRef(id: String? = null, initial: () -> S): Ref<S> {
    val key = createKey(id, initial, "useRef")
    val value = data.hooks.getOrPut(key, initial)

    return Ref(key, value as S)
}

interface RefContext<P : Any> {
    val data: Data<P>

    var <S> Ref<S>.current: S
        get() = data.hooks[key]!! as S
        set(v) { data.hooks[key] = v }

    operator fun<S> Ref<S>.invoke(updater: S.() -> Unit) {
        updater(current)
    }

    infix fun<S> Ref<S>.set(value: S) {
        this.current = value
    }

    infix fun<S> Ref<S>.set(value: (prev: S) -> S) {
        this.current = value(this.current)
    }

    val Ref<out Collection<*>>.size
        get() = current.size

    infix fun<E> Ref<E>.eq(other: E) = current == other

    fun<E> Ref<out Iterable<E>>.forEach(body: (E) -> Unit) = current.forEach(body)
    fun<E> Ref<out Iterable<E>>.forEachIndex(body: (Int, E) -> Unit) = current.forEachIndexed(body)

    fun<E> Ref<out Iterable<E>>.indexOf(element: E) = current.indexOf(element)

    fun<E, R> Ref<out Iterable<E>>.map(mapper: (E) -> R) = current.map(mapper)
    fun<E, R> Ref<out Iterable<E>>.mapIndex(mapper: (Int, E) -> R) = current.mapIndexed(mapper)

    fun<S> Ref<S>.asString() = raw.toString()
}


class Ref<S>(
    val key: HookKey,
    /**
     * Never access this variable directly
     */
    internal var raw: S
) {
    val id by key::id
}