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
fun<S: Any> RenderContext<*, *>.useState(initial: () -> S) = useState(generateId(initial), initial)

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

    infix fun<S> State<S>.update(updater: S.() -> Unit) {
        updater(value)
    }

    operator fun<S> State<S>.timesAssign(value: S) { this.value = value }

    infix fun<S> State<S>.set(value: (prev: S) -> S) {
        this.value = value(this.value)
    }

    fun<S> State<S>.asString() = raw.toString()
}