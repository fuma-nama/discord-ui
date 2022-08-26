package modal

import context.Container
import context.RenderContainer
import context.RenderContext
import listeners.ModalHandler
import listeners.modal
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal
import utils.lambdaList

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

fun<P: Any> RenderContext<P, *>.useModal(init: ModalBuilder<P>.() -> Unit): Modal {
    with (ModalBuilder(this).apply(init)) {

        return Modal.create(id, title)
            .addActionRows(list)
            .build()
    }
}

fun<P: Any> RenderContext<P, *>.useModal(factory: ModalFactory, handler: ModalHandler<P>): Modal {
    val id = modal(handler)

    return factory.build(id)
}

class ModalBuilder<P: Any>(context: RenderContext<P, *>): RenderContainer<ActionRow, P>(context) {
    lateinit var title: String
    lateinit var id: String

    fun submit(handler: ModalHandler<P>) {
        id = context.modal(handler)
    }
}

fun Modal.open(event: IModalCallback) {
    event.replyModal(this).queue()
}

operator fun ModalInteractionEvent.get(id: String): String {
    return this.getValue(id)!!.asString
}