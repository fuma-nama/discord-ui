package net.sonmoosans.dui.context

import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.hooks.Context
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.MessageBuilder
import net.sonmoosans.dui.listeners.on

@DslMarker
annotation class DslBuilder

class RenderContextEdit<P: Any, C: Component<P>>(
    data: Data<P>,
    component: C,
    override val builder: MessageEditBuilder = MessageEditBuilder().setReplace(true)
) :
    RenderContext<P, C>(
        data, component
    )

class RenderContextCreate<P: Any, C: Component<P>>(
    data: Data<P>,
    component: C,
    override val builder: MessageCreateBuilder = MessageCreateBuilder()
) :
    RenderContext<P, C>(
        data, component
    )

class RenderContextImpl<P: Any, C: Component<P>>(
    data: Data<P>,
    component: C,
    override val builder: MessageBuilder
) : RenderContext<P, C>(data, component)

abstract class RenderContext<P: Any, C: Component<P>>(
    data: Data<P>,
    component: C
): DataContext<C, P>(data, component), IDScope {
    abstract val builder: MessageBuilder

    fun<E: GenericComponentInteractionCreateEvent> interaction(
        id: String? = null,
        handler: InteractionContext<E, C, P>.() -> Unit
    ) = on(id, handler)

    fun modal(
        id: String? = null,
        handler: ModalContext<P, C>.() -> Unit
    ) = on(id, handler)

    /**
     * Current ID Scope, used for avoiding ID duplication
     */
    override var scope = ""
    var contexts: Map<Context<*>, Any?>? = null

    /**
     * Run lambda if render mode is edit
     */
    inline fun onEdit(run: RenderContextEdit<P, C>.() -> Unit) {
        if (this is RenderContextEdit) {
            run(this)
        }
    }

    /**
     * Run lambda if render mode is create
     */
    inline fun onCreate(run: RenderContextCreate<P, C>.() -> Unit) {
        if (this is RenderContextCreate) {
            run(this)
        }
    }

    /**
     * Force renders another component, ignoring the data of component
     *
     * We don't recommend to use nested components, use extension function instead
     */
    operator fun Component<P>.invoke() = this.render.invoke(this@RenderContext as RenderContext<P, Component<P>>)

    fun<T : Any> Context<T>.provider(value: T, children: RenderContext<P, C>.() -> Unit) {
        val prev = contexts

        contexts = HashMap(prev).also {
            it[this] = value
        }

        children(this@RenderContext)

        contexts = prev
    }
}

