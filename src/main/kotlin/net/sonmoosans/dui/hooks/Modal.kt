package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.context.RenderContainer
import net.sonmoosans.dui.context.RenderContext
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Modal
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.context.ModalContext
import net.sonmoosans.dui.listeners.Handler
import net.sonmoosans.dui.utils.ModalFactory

private typealias ModalHandler<P, C> = Handler<ModalContext<P, C>>

fun<D: Data<P>, P: Any> RenderContext<D, P>.useModal(init: ModalBuilder<D, P>.() -> Unit): Modal {
    with (ModalBuilder(this).apply(init)) {

        return Modal.create(id, title)
            .addActionRows(list)
            .build()
    }
}

fun<D: Data<P>, P: Any> RenderContext<D, P>.useModalLazy(init: ModalBuilder<D, P>.() -> Unit) =
    ModalHook { useModal(init) }

/**
 * @param dynamic If enabled, use Memory-Safe Dynamic Listener. Otherwise, use Data Based Listener
 */
fun<D: Data<P>, P: Any> RenderContext<D, P>.useModal(factory: ModalFactory, id: String? = null, dynamic: Boolean = this.dynamic, handler: ModalHandler<D, P>): Modal {
    val listener = modal(id, dynamic, handler)

    return factory.build(listener)
}

class ModalBuilder<D: Data<P>, P: Any>(context: RenderContext<D, P>): RenderContainer<ActionRow, D, P>(context) {
    lateinit var title: String
    lateinit var id: String

    /**
     * @param dynamic If enabled, use Memory-Safe Dynamic Listener. Otherwise, use Data Based Listener
     */
    fun submit(id: String? = null, dynamic: Boolean = context.dynamic, handler: ModalHandler<D, P>) {
        this.id = context.modal(id, dynamic, handler)
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