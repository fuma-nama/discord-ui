package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.context.RenderContainer
import net.sonmoosans.dui.context.RenderContext
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.context.ModalContext
import net.sonmoosans.dui.listeners.Handler
import net.sonmoosans.dui.utils.ModalFactory

private typealias ModalHandler<P, C> = Handler<ModalContext<P, C>>

fun<P: Any, C: Component<P>> RenderContext<P, C>.useModal(init: ModalBuilder<P, C>.() -> Unit): Modal {
    with (ModalBuilder(this).apply(init)) {

        return Modal.create(id, title)
            .addActionRows(list)
            .build()
    }
}

fun<P: Any, C: Component<P>> RenderContext<P, C>.useModalLazy(init: ModalBuilder<P, C>.() -> Unit) =
    ModalHook { useModal(init) }

fun<P: Any, C: Component<P>> RenderContext<P, C>.useModal(factory: ModalFactory, id: String? = null, handler: ModalHandler<P, C>): Modal {
    val listener = modal(id, handler)

    return factory.build(listener)
}

class ModalBuilder<P: Any, C: Component<P>>(context: RenderContext<P, C>): RenderContainer<ActionRow, C, P>(context) {
    lateinit var title: String
    lateinit var id: String

    fun submit(id: String? = null, handler: ModalHandler<P, C>) {
        this.id = context.modal(id, handler)
    }
}

/**
 * Render Modal when access it
 */
fun interface ModalHook {
    fun create(): Modal

    operator fun<P> getValue(parent: P, prop: Any): Modal {
        return create()
    }
}