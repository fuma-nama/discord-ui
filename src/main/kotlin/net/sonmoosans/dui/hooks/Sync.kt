package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.HookKey
import net.sonmoosans.dui.context.RenderContext
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.DataContext

/**
 * Sync updating multi messages
 *
 * Notice that useSync hook always use Root ID Scope instead
 */
fun RenderContext<*, *>.useSync(id: String = ""): Sync {
    val key = HookKey(id, "useSync")
    var hook = data.hooks[key] as SyncHook?

    if (hook == null) {
        hook = SyncHook()
        data.hooks[key] = hook
    }

    return Sync(hook)
}

fun<P: Any> Data<P>.sync(event: InteractionHook, id: String = "") {
    val key = HookKey(id, "useSync")
    val hook = hooks[key] as SyncHook?

    if (hook != null) {
        hook.hooks += event
    } else {
        hooks[key] = SyncHook(arrayListOf(event))
    }
}

fun<P: Any> Data<P>.unsync(event: InteractionHook, id: String = "") {
    val key = HookKey(id, "useSync")

    val hook = hooks[key] as SyncHook?

    if (hook != null) {
        hook.hooks -= event
    }
}

class SyncHook(
    val hooks: ArrayList<InteractionHook> = arrayListOf()
)

class Sync(val hook: SyncHook)

interface SyncContext<P : Any> {
    val context: DataContext<P>

    private fun edit() = context.component.edit(context.data)

    fun Sync.edit(event: IMessageEditCallback) {
        val rendered = edit()

        event.editMessage(rendered).queue {
            invoke(it)
        }
    }

    fun Sync.delete() {

        for (hook in hook.hooks) {
            hook.deleteOriginal().queue()
        }

        context.destroy()
    }

    fun Sync.delete(event: IMessageEditCallback) {

        event.deferEdit().queue {
            delete()
        }
    }

    operator fun Sync.invoke(
        event: InteractionHook,
        rendered: MessageEditData = edit()
    ) {

        event.retrieveOriginal().queue { original ->
            hook.hooks.forEach {

                if (it.interaction.id != original.interaction?.id) {
                    it.editOriginal(rendered).queue()
                }
            }
        }
    }

    operator fun Sync.invoke() {
        val rendered = edit()

        hook.hooks.forEach {
            it.editOriginal(rendered).queue()
        }
    }
}