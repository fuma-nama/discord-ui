package hooks

import Data
import HookKey
import context.DataContext
import context.RenderContext
import net.dv8tion.jda.api.interactions.InteractionHook

private fun<P: Any> DataContext<P>.sync(hook: SyncHook, event: InteractionHook) {
    val rendered = this.component.edit(this.id, this.data)

    event.retrieveOriginal().queue { original ->
        hook.hooks.forEach {

            if (it.interaction.id != original.interaction?.id) {
                it.editOriginal(rendered).queue()
            }
        }
    }
}

fun RenderContext<*, *>.useSync(id: String = ""): (InteractionHook) -> Unit {
    val key = HookKey(id, "useSync")
    var hook = data.hooks[key] as SyncHook?

    if (hook == null) {
        hook = SyncHook()
        data.hooks[key] = hook
    }

    return { event -> sync(hook, event) }
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
