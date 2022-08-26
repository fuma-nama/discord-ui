package context

import Component
import Data
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent

class InteractionContext<E: GenericComponentInteractionCreateEvent, P : Any>(
    val event: E, id: String, data: Data<P>, comp: Component<P>
): DataContext<P>(id, data, comp)