package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.HookKey
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.utils.createKey
import net.sonmoosans.dui.utils.generateId

/**
 * Create a State
 *
 * State will be memorized every renders
 */
fun<S> RenderContext<*, *>.useState(id: String, initial: () -> S): S {
    val key = createKey(id, initial, "useState")

    val cache = data.hooks[key]

    if (cache != null) {
        return cache as S
    }

    val state = initial()

    data.hooks[key] = state
    return state
}

/**
 * Generates an ID from initial lambda's class
 */
fun<S> RenderContext<*, *>.useState(initial: () -> S) = useState(generateId(initial), initial)

fun<S> RenderContext<*, *>.useState(id: String, initial: S) = useState(id) { initial }

fun<S> RenderContext<*, *>.useRef(id: String? = null, initial: () -> S): Ref<S> {
    val key = createKey(id, initial, "useRef")
    val ref = data.hooks[key] as S?

    return if (ref != null) {
        Ref(key, ref)
    } else {
        Ref(key, initial()).also {
            data.hooks[key] = it
        }
    }
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

    operator fun<E> Ref<out Iterable<E>>.plus(element: E) = current + element

    operator fun<E> Ref<out Iterable<E>>.plusAssign(element: E) { current += element }

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