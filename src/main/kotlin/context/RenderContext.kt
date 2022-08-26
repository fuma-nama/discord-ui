package context

import Component
import Data
import MessageBuilder
import listeners.*
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder

@DslMarker
annotation class DslBuilder

class RenderContextEdit<P: Any>(id: String, data: Data<P>, component: Component<P>) :
    RenderContext<P, MessageEditBuilder>(
    id, data, MessageEditBuilder(), component
)

class RenderContextCreate<P: Any>(id: String, data: Data<P>, component: Component<P>) :
    RenderContext<P, MessageCreateBuilder>(
    id, data, MessageCreateBuilder(), component
)

open class RenderContext<P: Any, B: MessageBuilder>(
    id: String,
    data: Data<P>,
    val builder: B,
    component: Component<P>
): DataContext<P>(id, data, component) {

    /**
     * Run lambda if render mode is edit
     */
    inline fun onEdit(run: RenderContextEdit<P>.() -> Unit) {
        if (this is RenderContextEdit) {
            run(this as RenderContextEdit<P>)
        }
    }

    /**
     * Run lambda if render mode is create
     */
    inline fun onCreate(run: RenderContextCreate<P>.() -> Unit) {
        if (this is RenderContextCreate) {
            run(this as RenderContextCreate<P>)
        }
    }

    fun<S: Any> useState(id: String, initial: S): State<S> {
        val cache = data.states[id]

        if (cache != null) {
            return cache as State<S>
        }

        val state = State(id, initial)

        data.states[id] = state
        return state
    }

    fun<S> useState(id: String): State<S?> {
        return State(id, null)
    }

}

class State<S>(
    val id: String,
    /**
     * Never access this variable directly
     */
    internal var raw: S
)