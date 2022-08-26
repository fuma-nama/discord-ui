package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.State
import net.sonmoosans.dui.utils.generateId

/**
 * Generates an ID from holder's class
 */
fun<S: Any> RenderContext<*, *>.useState(initial: S, holder: () -> Unit) = useState(generateId(id, holder), initial)

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