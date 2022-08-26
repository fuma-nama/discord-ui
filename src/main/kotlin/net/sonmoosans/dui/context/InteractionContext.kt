package net.sonmoosans.dui.context

import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.sonmoosans.dui.Component

class InteractionContext<E: GenericComponentInteractionCreateEvent, P : Any>(
    val event: E, id: String, data: Data<P>, comp: Component<P>
): DataContext<P>(id, data, comp)