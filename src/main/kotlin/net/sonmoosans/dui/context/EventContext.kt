package net.sonmoosans.dui.context

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data

open class EventContext<E, C: Component<P>, P : Any>(val event: E, data: Data<P>, comp: C): DataContext<C, P>(data, comp)

class InteractionContext<E: GenericComponentInteractionCreateEvent, C: Component<P>, P : Any>(
    event: E, data: Data<P>, comp: C
): EventContext<E, C, P>(event, data, comp)

class ModalContext<P: Any, C: Component<P>>(
    event: ModalInteractionEvent, data: Data<P>, component: C
): EventContext<ModalInteractionEvent, C, P>(event, data, component)