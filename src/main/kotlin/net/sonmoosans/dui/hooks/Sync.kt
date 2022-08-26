package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.HookKey
import net.sonmoosans.dui.context.RenderContext
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.utils.messages.MessageEditData

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
    val id: String
    val data: Data<P>
    val component: Component<P>

    fun Sync.edit(event: IMessageEditCallback) {
        val rendered = component.edit(id, data)

        event.editMessage(rendered).queue {
            invoke(it)
        }
    }

    operator fun Sync.invoke(
        event: InteractionHook,
        rendered: MessageEditData = component.edit(id, data)
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
        val rendered = component.edit(id, data)

        hook.hooks.forEach {
            it.editOriginal(rendered).queue()
        }
    }
}