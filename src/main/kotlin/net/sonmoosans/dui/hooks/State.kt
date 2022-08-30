package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.State
import net.sonmoosans.dui.utils.generateId

/**
 * Create a State
 *
 * State will be memorized every renders
 */
fun<S> RenderContext<*, *>.useState(id: String, initial: () -> S): State<S> {
    val key = createId(id)

    val cache = data.states[key]

    if (cache != null) {
        return cache as State<S>
    }

    val state = State(key, initial())

    data.states[key] = state
    return state
}

/**
 * Generates an ID from initial lambda's class
 */
fun<S> RenderContext<*, *>.useState(initial: () -> S) = useState(generateId(initial), initial)

fun<S> RenderContext<*, *>.useState(id: String, initial: S) = useState(id) { initial }

interface StateContext<P> {
    val data: Data<P>

    val<S> State<S>.current
        get() = data.states[id]!! as State<S>

    var <S> State<S>.value: S
        get() = current.raw
        set(v) {
            current.raw = v
        }

    operator fun<S> State<S>.invoke(updater: S.() -> Unit) {
        updater(value)
    }

    infix fun<S> State<S>.set(value: S) {
        this.value = value
    }

    infix fun<S> State<S>.set(value: (prev: S) -> S) {
        this.value = value(this.value)
    }

    operator fun<E> State<out Iterable<E>>.plus(element: E) = value + element
    operator fun State<Int>.plus(other: Int) = value + other
    operator fun State<Double>.plus(other: Double) = value + other

    operator fun<E> State<out Iterable<E>>.plusAssign(element: E) { value += element }
    operator fun State<Int>.plusAssign(other: Int) { value += other }
    operator fun State<Double>.plusAssign(other: Double) { value += other }

    fun<E> State<out Iterable<E>>.minusAssign(element: E) { value -= element }
    operator fun State<Int>.minusAssign(other: Int) { value -= other }
    operator fun State<Double>.minusAssign(other: Double) { value -= other }

    operator fun State<Int>.timesAssign(other: Int) { value *= other }
    operator fun State<Double>.timesAssign(other: Double) { value *= other }

    operator fun State<Int>.divAssign(other: Int) { value /= other }
    operator fun State<Double>.divAssign(other: Double) { value /= other }

    operator fun State<Int>.remAssign(other: Int) { value %= other }
    operator fun State<Double>.remAssign(other: Double) { value %= other }

    infix fun<E> State<E>.eq(other: E) = value == other
    operator fun State<Int>.compareTo(other: Int) = value.compareTo(other)
    operator fun State<Double>.compareTo(other: Double) = value.compareTo(other)

    fun<E> State<out Iterable<E>>.forEach(body: (E) -> Unit) = value.forEach(body)
    fun<E> State<out Iterable<E>>.forEachIndex(body: (Int, E) -> Unit) = value.forEachIndexed(body)

    fun<E> State<out Iterable<E>>.indexOf(element: E) = value.indexOf(element)

    fun<E, R> State<out Iterable<E>>.map(mapper: (E) -> R) = value.map(mapper)
    fun<E, R> State<out Iterable<E>>.mapIndex(mapper: (Int, E) -> R) = value.mapIndexed(mapper)

    fun<S> State<S>.asString() = raw.toString()
}