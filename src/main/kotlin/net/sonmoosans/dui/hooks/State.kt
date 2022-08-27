package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.State
import net.sonmoosans.dui.utils.generateId

/**
 * Generates an ID from holder's class
 */
fun<S: Any> RenderContext<*, *>.useState(initial: S, holder: () -> Unit) = useState(generateId(null, holder), initial)

fun<S> RenderContext<*, *>.useState(id: String, initial: S): State<S> {
    val cache = data.states[id]

    if (cache != null) {
        return cache as State<S>
    }

    val state = State(id, initial)

    data.states[id] = state
    return state
}

fun<S: Any, P: Any> RenderContext<P, *>.useState(id: String) = useState<S?>(id, null)

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