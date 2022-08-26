package context

import Component
import Data
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

class ModalContext<P: Any>(
    val event: ModalInteractionEvent, id: String, data: Data<P>, component: Component<P>
): DataContext<P>(id, data, component)
