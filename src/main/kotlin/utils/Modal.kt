package utils

import context.Container
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal

fun modal(title: String, init: Container<ActionRow>.() -> Unit): ModalFactory {
    return ModalFactory { id ->
        Modal.create(title, id)
            .addActionRows(lambdaList(init))
            .build()
    }
}

fun interface ModalFactory {
    fun build(id: String): Modal
}

fun Modal.open(event: IModalCallback) {
    event.replyModal(this).queue()
}

operator fun ModalInteractionEvent.get(id: String): String {
    return this.getValue(id)!!.asString
}