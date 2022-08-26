package net.sonmoosans.dui.context

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.hooks.Context
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.MessageBuilder

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
    val contexts by lazy { hashMapOf<Context<*>, Any?>() }

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

    fun<C : Any> Context<C>.provider(value: C, children: RenderContext<P, B>.() -> Unit) {
        contexts[this] = value

        children(this@RenderContext)
        contexts.remove(this)
    }
}

class State<S>(
    val id: String,
    /**
     * Never access this variable directly
     */
    internal var raw: S
)
