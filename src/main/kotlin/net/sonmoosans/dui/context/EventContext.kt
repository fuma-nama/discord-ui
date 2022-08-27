package net.sonmoosans.dui.context

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data

open class EventContext<E, P : Any>(val event: E, data: Data<P>, comp: Component<P>): DataContext<P>(data, comp)

class InteractionContext<E: GenericComponentInteractionCreateEvent, P : Any>(
    event: E, data: Data<P>, comp: Component<P>
): EventContext<E, P>(event, data, comp)

class ModalContext<P: Any>(
    event: ModalInteractionEvent, data: Data<P>, component: Component<P>
): EventContext<ModalInteractionEvent, P>(event, data, component)