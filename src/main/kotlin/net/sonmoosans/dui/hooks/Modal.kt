package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.context.RenderContainer
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.listeners.modal
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal
import net.sonmoosans.dui.context.ModalContext
import net.sonmoosans.dui.listeners.Handler
import net.sonmoosans.dui.utils.ModalFactory

private typealias ModalHandler<P> = Handler<ModalContext<P>>

fun<P: Any> RenderContext<P, *>.useModal(init: ModalBuilder<P>.() -> Unit): Modal {
    with (ModalBuilder(this).apply(init)) {

        return Modal.create(id, title)
            .addActionRows(list)
            .build()
    }
}

fun<P: Any> RenderContext<P, *>.useModal(factory: ModalFactory, id: String? = null, handler: ModalHandler<P>): Modal {
    val listener = modal(id, handler)

    return factory.build(listener)
}

class ModalBuilder<P: Any>(context: RenderContext<P, *>): RenderContainer<ActionRow, P>(context) {
    lateinit var title: String
    lateinit var id: String

    fun submit(id: String? = null, handler: ModalHandler<P>) {
        this.id = context.modal(id, handler)
    }
}