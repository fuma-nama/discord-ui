package net.sonmoosans.dui

import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.sonmoosans.dui.listeners.Handler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.EventContext

typealias Element<P> = RenderContext<out Data<P>, P>
typealias MessageBuilder = AbstractMessageBuilder<*, *>

abstract class AbstractComponent<D: Data<P>, P: Any, C: AbstractComponent<D, P, C>>(
    override val render: RenderContext<D, P>.() -> Unit
) : Component<D, P> {
    override val listeners = hashMapOf<String, Handler<EventContext<*, D, P>>>()

    override fun listen(id: String, listener: Handler<EventContext<*, D, P>>) {
        listeners[id] = listener
    }
}

interface Component<D: Data<P>, P: Any> {
    /**
     * Whether to Use Dynamic Listeners in default
     */
    val dynamic: Boolean get() = false
    val render: RenderContext<D, P>.() -> Unit
    val listeners: Map<String, Handler<EventContext<*, D, P>>>

    fun listen(id: String, listener: Handler<EventContext<*, D, P>>)

    /**
     * parse Data from Listener Data
     */
    fun parseData(data: String): D?

    /**
     * Encode Listener Data
     */
    fun encodeData(data: D): String

    /**
     * renders Component
     */
    fun render(data: D): MessageCreateData {
        val context = RenderContextCreate(data, this)

        render(context)

        return context.builder.build()
    }

    /**
     * Parse data from id and renders Component
     */
    fun edit(data: D): MessageEditData {
        val context = RenderContextEdit(data, this)

        render(context)

        return context.builder.build()
    }

    fun destroy(data: D)
}