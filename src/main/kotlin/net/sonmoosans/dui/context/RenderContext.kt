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

class RenderContextEdit<D: Data<P>, P: Any>(
    data: D,
    component: Component<D, P>,
    override val builder: MessageEditBuilder = MessageEditBuilder().setReplace(true)
) :
    RenderContext<D, P>(
        data, component
    )

class RenderContextCreate<D: Data<P>, P: Any>(
    data: D,
    component: Component<D, P>,
    override val builder: MessageCreateBuilder = MessageCreateBuilder()
) :
    RenderContext<D, P>(
        data, component
    )

class RenderContextImpl<D: Data<P>, P: Any>(
    data: D,
    component: Component<D, P>,
    override val builder: MessageBuilder
) : RenderContext<D, P>(data, component)

abstract class RenderContext<D: Data<P>, P: Any>(
    data: D,
    component: Component<D, P>
): DataContext<D, P>(data, component), IDScope {
    abstract val builder: MessageBuilder

    /**
     * Listener type to use when not specified
     *
     * Use Data based Listeners in default
     */
    var dynamic: Boolean = false

    /**
     * Listen Interaction Events
     * @param dynamic If enabled, use Memory-Safe Dynamic Listener. Otherwise, use Data Based Listener
     */
    fun<E: GenericComponentInteractionCreateEvent> interaction(
        id: String? = null,
        dynamic: Boolean = this.dynamic,
        handler: InteractionContext<E, D, P>.() -> Unit
    ) = on(id, dynamic, handler)

    /**
     * Listen Modal Events
     * @param dynamic If enabled, use Memory-Safe Dynamic Listener. Otherwise, use Data Based Listener
     */
    fun modal(
        id: String? = null,
        dynamic: Boolean = this.dynamic,
        handler: ModalContext<D, P>.() -> Unit
    ) = on(id, dynamic, handler)

    /**
     * Current ID Scope, used for avoiding ID duplication
     */
    override var scope = ""
    var contexts: Map<Context<*>, Any?>? = null

    /**
     * Run lambda if render mode is edit
     */
    inline fun onEdit(run: RenderContextEdit<D, P>.() -> Unit) {
        if (this is RenderContextEdit) {
            run(this)
        }
    }

    /**
     * Run lambda if render mode is create
     */
    inline fun onCreate(run: RenderContextCreate<D, P>.() -> Unit) {
        if (this is RenderContextCreate) {
            run(this)
        }
    }

    /**
     * Force renders another component, ignoring the data of component
     *
     * We don't recommend to use nested components, use extension function instead
     */
    operator fun Component<D, P>.invoke() = this.render.invoke(this@RenderContext)

    fun<T : Any> Context<T>.provider(value: T, children: RenderContext<D, P>.() -> Unit) {
        val prev = contexts

        contexts = HashMap(prev).also {
            it[this] = value
        }

        children(this@RenderContext)

        contexts = prev
    }
}

