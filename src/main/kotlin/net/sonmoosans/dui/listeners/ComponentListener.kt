package net.sonmoosans.dui.listeners

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.dui.context.*

typealias Handler<E> = E.() -> Unit

fun<C: EventContext<*, P>, P : Any> RenderContext<P, *>.on(
    handler: Handler<C>
): String {
    val listenerId = component.listen(
        handler as Handler<EventContext<*, P>>
    )

    return ComponentListener.listen(component, data, listenerId)
}

fun<E: GenericComponentInteractionCreateEvent, P : Any> RenderContext<P, *>.interaction(
    handler: InteractionContext<E, P>.() -> Unit
) = on(handler)

fun<P : Any> RenderContext<P, *>.modal(
    handler: ModalContext<P>.() -> Unit
) = on(handler)

data class RawId(val comp: Int, val dataId: Long, val listenerId: Int)

object ComponentListener : ListenerAdapter() {
    /** Pair<ComponentId, Component> **/
    val components = hashMapOf<Int, Component<*>>()
    var encoder: Encoder = DefaultEncoder()

    fun listen(component: Component<*>, data: Data<*>, listener: Int): String {
        components[component.hashCode()] = component

        return encoder.encodeId(component, data.id, listener)
    }

    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) = handle<Any>(event)
    override fun onModalInteraction(event: ModalInteractionEvent) = handle<Any>(event)

    private fun<P: Any> handle(event: GenericComponentInteractionCreateEvent) {
        val (compId, dataId, modalId) = encoder.decodeId(event.componentId)

        val comp = components[compId]?: return
        val data = comp.store[dataId]?: return
        val listener = comp.listeners[modalId]?: return

        (listener as Handler<InteractionContext<*, P>>).invoke(
            InteractionContext(event, data as Data<P>, comp as Component<P>)
        )
    }

    private fun<P: Any> handle(event: ModalInteractionEvent) {
        val (compId, dataId, modalId) = encoder.decodeId(event.modalId)

        val comp = components[compId]?: return
        val data = comp.store[dataId]?: return
        val modal = comp.listeners[modalId]?: return

        (modal as Handler<ModalContext<P>>).invoke(
            ModalContext(event, data as Data<P>, comp as Component<P>)
        )
    }
}

interface Encoder {
    fun encodeId(comp: Component<*>, dataId: Long, listenerId: Int): String

    fun decodeId(id: String): RawId
}

class DefaultEncoder : Encoder {
    override fun encodeId(comp: Component<*>, dataId: Long, listenerId: Int): String {

        return "${comp.hashCode()}-$dataId-$listenerId"
    }

    override fun decodeId(id: String): RawId {
        val (compId, dataId, listenerId) = id.split("-")

        return RawId(compId.toInt(), dataId.toLong(), listenerId.toInt())
    }
}