package net.sonmoosans.dui.listeners

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.dui.context.InteractionContext
import net.sonmoosans.dui.context.ModalContext
import net.sonmoosans.dui.context.RenderContext

typealias Handler<E, P> = InteractionContext<E, P>.() -> Unit
typealias ModalHandler<P> = ModalContext<P>.() -> Unit

/** Pair<ComponentId, Component> **/
val components = hashMapOf<Int, Component<*>>()

private fun encodeId(comp: Component<*>, dataId: String, listenerId: Int): String {
    return "${comp.hashCode()}-$dataId-$listenerId"
}

private fun decodeId(id: String): RawId<*>? {
    val (compId, dataId, listenerId) = id.split("-")
    val comp = components[compId.toInt()]?: return null

    return RawId(comp, dataId, listenerId.toInt())
}

fun listen(component: Component<*>) {
    components[component.hashCode()] = component
}

fun<E: GenericComponentInteractionCreateEvent, P : Any> RenderContext<P, *>.interaction(
    handler: InteractionContext<E, P>.() -> Unit
): String {
    listen(component)
    val listenerId = handler::class.hashCode()
    val id = encodeId(this.component, this.id, listenerId)

    component.listeners[listenerId] = handler as Handler<*, P>

    return id
}

fun<P : Any> RenderContext<P, *>.modal(
    handler: ModalHandler<P>
): String {
    listen(component)
    val modalId = handler::class.hashCode()
    val id = encodeId(this.component, this.id, modalId)

    component.modals[modalId] = handler

    return id
}

data class RawId<P: Any>(val comp: Component<P>, val dataId: String, val listenerId: Int)

class ComponentListener : ListenerAdapter() {
    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) = handle<Any>(event)
    override fun onModalInteraction(event: ModalInteractionEvent) = handle<Any>(event)

    private fun<P: Any> handle(event: GenericComponentInteractionCreateEvent) {
        val (comp, dataId, listenerId) = decodeId(event.componentId) ?: return
        val data = comp.store[dataId]?: return
        val listener = comp.listeners[listenerId]?: return

        (listener as Handler<*, P>).invoke(
            InteractionContext(event, dataId, data as Data<P>, comp as Component<P>)
        )
    }

    private fun<P: Any> handle(event: ModalInteractionEvent) {
        val (comp, dataId, modalId) = decodeId(event.modalId) ?: return
        val data = comp.store[dataId]?: return
        val modal = comp.modals[modalId]?: return

        (modal as ModalHandler<P>).invoke(
            ModalContext(event, dataId, data as Data<P>, comp as Component<P>)
        )
    }
}