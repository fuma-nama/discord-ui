package listeners

import Component
import Data
import context.*
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

typealias Handler<E, P> = InteractionContext<E, P>.() -> Unit

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

data class RawId<P: Any>(val comp: Component<P>, val dataId: String, val listenerId: Int)

class ComponentListener : ListenerAdapter() {
    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) = handle<Any>(event)

    private fun<P: Any> handle(event: GenericComponentInteractionCreateEvent) {
        val (comp, dataId, listenerId) = decodeId(event.componentId)?: return
        val data = comp.store[dataId]?: return
        val listener = comp.listeners[listenerId]?: return

        (listener as Handler<*, P>).invoke(
            InteractionContext(event, dataId, data as Data<P>, comp as Component<P>)
        )
    }
}