package net.sonmoosans.dui

import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.sonmoosans.dui.listeners.Handler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.EventContext

typealias Element<P> = RenderContext<P, out Component<P>>
typealias MessageBuilder = AbstractMessageBuilder<*, *>



abstract class AbstractComponent<P: Any, C: AbstractComponent<P, C>>(
    render: RenderContext<P, C>.() -> Unit
) : Component<P> {
    override val render = render as RenderContext<P, Component<P>>.() -> Unit
    override val listeners = hashMapOf<String, Handler<EventContext<*, out Component<P>, P>>>()

    override fun listen(id: String, listener: Handler<EventContext<*, out Component<P>, P>>) {
        listeners[id] = listener
    }
}

interface Component<P : Any> {
    val listeners: Map<String, Handler<EventContext<*, *, P>>>
    val render: RenderContext<P, Component<P>>.() -> Unit

    fun getData(id: Long): Data<P>?

    fun listen(id: String, listener: Handler<EventContext<*, *, P>>)

    /**
     * renders Component
     */
    fun render(data: Data<P>): MessageCreateData {
        val context = RenderContextCreate(data, this)

        render(context)

        return context.builder.build()
    }

    /**
     * Parse data from id and renders Component
     */
    fun edit(data: Data<P>): MessageEditData {
        val context = RenderContextEdit(data, this)

        render(context)

        return context.builder.build()
    }

    fun destroy(data: Data<P>)
}