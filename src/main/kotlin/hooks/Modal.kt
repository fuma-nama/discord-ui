package hooks

import context.RenderContainer
import context.RenderContext
import listeners.ModalHandler
import listeners.modal
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal
import utils.ModalFactory

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