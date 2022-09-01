package net.sonmoosans.dui.context

import net.sonmoosans.dui.Data
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.hooks.SyncContext
import net.sonmoosans.dui.hooks.StateContext
import net.sonmoosans.dui.utils.LocaleBuilder
import net.sonmoosans.dui.utils.LocalePair

@DslBuilder
open class DataContext<C: Component<P>, P : Any>(
    final override val data: Data<P>,
    val component: C
): StateContext<P>, SyncContext<C, P> {
    var props by data::props

    fun render() = component.render(data)
    override fun renderEdit() = component.edit(data)

    /**
     * Delete the Message
     * @param destroy If enabled, Data will be destroyed after delete
     */
    fun IMessageEditCallback.delete(destroy: Boolean = true) {

        deferEdit().queue {
            it.deleteOriginal().queue {
                if (destroy) {
                    destroy()
                }
            }
        }
    }

    fun IMessageEditCallback.edit() = with (this@DataContext) {
        editMessage(component.edit(data)).queue()
    }

    fun IMessageEditCallback.ignore() {
        deferEdit().queue()
    }

    fun IReplyCallback.reply() = with (this@DataContext) {
        reply(component.render(data)).queue()
    }

    override fun destroy() {
        component.destroy(data)
    }
}