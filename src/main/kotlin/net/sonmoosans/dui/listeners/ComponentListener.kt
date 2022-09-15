package net.sonmoosans.dui.listeners

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.dui.context.*
import net.sonmoosans.dui.utils.createId

typealias Handler<E> = E.() -> Unit

/**
 * dynamic listeners will only be used When no matching listeners in data
 */
fun<E: EventContext<*, D, P>, D: Data<P>, P : Any> RenderContext<D, P>.on(
    id: String? = null,
    dynamic: Boolean,
    handler: Handler<E>,
): String {
    val listenerId = createId(id, handler)
    if (dynamic) component.listen(
        listenerId,
        handler as Handler<EventContext<*, D, P>>
    ) else {
        data.listeners[listenerId] = handler as Handler<EventContext<*, Data<P>, P>>
    }

    return ComponentListener.listen(component, component.encodeData(data), listenerId)
}

data class RawId(val comp: Int, val dataId: String, val listenerId: String) {
    fun<D: Data<P>, P: Any, E> build(): DynamicId<D, P, E>? {
        val comp = ComponentListener.components[this.comp]?: return null
        val data = comp.parseData(this.dataId)?: return null
        val listener = data.listeners[listenerId]?: comp.listeners[listenerId]
        listener?: return null

        return DynamicId(comp as Component<D, P>, data as D, listener as Handler<E>)
    }
}

data class DynamicId<D: Data<P>, P: Any, E>(val comp: Component<D, P>, val data: D, val listener: Handler<E>)

object ComponentListener : ListenerAdapter() {
    /** Pair<ComponentId, Component> **/
    val components = hashMapOf<Int, Component<*, *>>()
    var encoder: Encoder = DefaultEncoder()

    fun listen(component: Component<*, *>, data: String, listener: String): String {
        components[component.hashCode()] = component

        return encoder.encodeId(component, data, listener)
    }

    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) = handle<Any>(event)
    override fun onModalInteraction(event: ModalInteractionEvent) = handle<Any>(event)

    private fun<P: Any> handle(event: GenericComponentInteractionCreateEvent) {
        val id = encoder.decodeId(event.componentId)?: return
        val (comp, data, listener) = id.build<Data<P>, P, InteractionContext<*, *, P>>()?: return

        listener.invoke(
            InteractionContext(event, data, comp)
        )
    }

    private fun<P: Any> handle(event: ModalInteractionEvent) {
        val id = encoder.decodeId(event.modalId)?: return
        val (comp, data, listener) = id.build<Data<P>, P, ModalContext<Data<P>, P>>()?: return

        listener.invoke(
            ModalContext(event, data, comp)
        )
    }
}

interface Encoder {
    fun encodeId(comp: Component<*, *>, data: String, listenerId: String): String

    fun decodeId(id: String): RawId?
}

class DefaultEncoder : Encoder {
    override fun encodeId(comp: Component<*, *>, data: String, listenerId: String): String {

        if (listenerId.contains("-")) error(
            "Listener Id cannot contains '-'"
        )

        val id = "${comp.hashCode()}-$data-$listenerId"

        if (id.length > 100) error(
            "Your Listener ID is too long: $id, try reduce Scopes amount or Listener ID length"
        )

        return id
    }

    override fun decodeId(id: String): RawId? {
        val (compId, dataId, listenerId) = id.split("-").verify {
            return null
        }

        return RawId(compId.toInt(), dataId, listenerId)
    }

    private inline fun List<String>.verify(onFail: () -> Unit): List<String> {
        if (size != 3) onFail()

        return this
    }
}