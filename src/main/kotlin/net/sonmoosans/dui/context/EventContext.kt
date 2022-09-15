package net.sonmoosans.dui.context

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data

open class EventContext<E, D: Data<P>, P : Any>(val event: E, data: D, comp: Component<D, P>): DataContext<D, P>(data, comp)

class InteractionContext<E: GenericComponentInteractionCreateEvent, D: Data<P>, P: Any>(
    event: E, data: D, comp: Component<D, P>
): EventContext<E, D, P>(event, data, comp)

class ModalContext<D: Data<P>, P: Any>(
    event: ModalInteractionEvent, data: D, component: Component<D, P>
): EventContext<ModalInteractionEvent, D, P>(event, data, component)