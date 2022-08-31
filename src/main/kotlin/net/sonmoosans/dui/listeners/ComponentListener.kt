package net.sonmoosans.dui.listeners

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.dui.context.*
import net.sonmoosans.dui.utils.createId

typealias Handler<E> = E.() -> Unit

fun<C: EventContext<*, P>, P : Any> RenderContext<P, *>.on(
    id: String? = null,
    handler: Handler<C>,
): String {
    val listenerId = component.listen(
        createId(id, handler),
        handler as Handler<EventContext<*, P>>
    )

    return ComponentListener.listen(component, data, listenerId)
}

fun<E: GenericComponentInteractionCreateEvent, P : Any> RenderContext<P, *>.interaction(
    id: String? = null,
    handler: InteractionContext<E, P>.() -> Unit
) = on(id, handler)

fun<P : Any> RenderContext<P, *>.modal(
    id: String? = null,
    handler: ModalContext<P>.() -> Unit
) = on(id, handler)

data class RawId(val comp: Int, val dataId: Long, val listenerId: String) {
    fun<P: Any, E> build(): DynamicId<P, E>? {
        val comp = ComponentListener.components[this.comp]?: return null
        val data = comp.getData(this.dataId)?: return null
        val listener = comp.listeners[this.listenerId]?: return null

        return DynamicId(comp as Component<P>, data as Data<P>, listener as Handler<E>)
    }
}

data class DynamicId<P: Any, E>(val comp: Component<P>, val data: Data<P>, val listener: Handler<E>)

object ComponentListener : ListenerAdapter() {
    /** Pair<ComponentId, Component> **/
    val components = hashMapOf<Int, Component<*>>()
    var encoder: Encoder = DefaultEncoder()

    fun listen(component: Component<*>, data: Data<*>, listener: String): String {
        components[component.hashCode()] = component

        return encoder.encodeId(component, data.id, listener)
    }

    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) = handle<Any>(event)
    override fun onModalInteraction(event: ModalInteractionEvent) = handle<Any>(event)

    private fun<P: Any> handle(event: GenericComponentInteractionCreateEvent) {
        val id = encoder.decodeId(event.componentId)?: return
        val (comp, data, listener) = id.build<P, InteractionContext<*, P>>()?: return

        listener.invoke(
            InteractionContext(event, data, comp)
        )
    }

    private fun<P: Any> handle(event: ModalInteractionEvent) {
        val id = encoder.decodeId(event.modalId)?: return
        val (comp, data, listener) = id.build<P, ModalContext<P>>()?: return

        listener.invoke(
            ModalContext(event, data, comp)
        )
    }
}

interface Encoder {
    fun encodeId(comp: Component<*>, dataId: Long, listenerId: String): String

    fun decodeId(id: String): RawId?
}

class DefaultEncoder : Encoder {
    override fun encodeId(comp: Component<*>, dataId: Long, listenerId: String): String {

        if (listenerId.contains("-")) error(
            "Listener Id cannot contains '-'"
        )

        val id = "${comp.hashCode()}-$dataId-$listenerId"

        if (id.length > 100) error(
            "Your Listener ID is too long: $id, try reduce Scopes amount or Listener ID length"
        )

        return id
    }

    override fun decodeId(id: String): RawId? {
        val (compId, dataId, listenerId) = id.split("-").verify {
            return null
        }

        return RawId(compId.toInt(), dataId.toLong(), listenerId)
    }

    private inline fun List<String>.verify(onFail: () -> Unit): List<String> {
        if (size != 3) onFail()

        return this
    }
}