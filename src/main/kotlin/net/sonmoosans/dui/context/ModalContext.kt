package net.sonmoosans.dui.context

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

class ModalContext<P: Any>(
    val event: ModalInteractionEvent, id: String, data: Data<P>, component: Component<P>
): DataContext<P>(id, data, component)
