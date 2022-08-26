package context

import Component
import Data
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent

class InteractionContext<E: GenericComponentInteractionCreateEvent, P : Any>(
    val event: E, id: String, data: Data<P>, comp: Component<P>
): DataContext<P>(id, data, comp) {

    /**
     * Delete the Message
     * @param destroy If enabled, Data will be destroyed after delete
     */
    fun delete(destroy: Boolean = true) {
        event.deferEdit().queue {
            it.deleteOriginal().queue {
                if (destroy) {
                    destroy()
                }
            }
        }
    }

    fun reply() {
        event.reply(component.render(id, data)).queue()
    }

    fun edit() {
        event.editMessage(component.edit(id, data)).queue()
    }
}