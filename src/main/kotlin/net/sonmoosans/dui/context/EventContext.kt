package net.sonmoosans.dui.context

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data

open class EventContext<E, P : Any>(val event: E, id: String, data: Data<P>, comp: Component<P>): DataContext<P>(id, data, comp)

class InteractionContext<E: GenericComponentInteractionCreateEvent, P : Any>(
    event: E, id: String, data: Data<P>, comp: Component<P>
): EventContext<E, P>(event, id, data, comp)

class ModalContext<P: Any>(
    event: ModalInteractionEvent, id: String, data: Data<P>, component: Component<P>
): EventContext<ModalInteractionEvent, P>(event, id, data, component)