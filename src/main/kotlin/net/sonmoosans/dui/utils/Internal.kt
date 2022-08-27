package net.sonmoosans.dui.utils

import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.MessageBuilder
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit

/**
 * Generate ID with the hashcode of the interface class
 */
fun generateId(id: Int?, func: Function<*>): Int {
    return id?: func::class.hashCode()
}

/**
 * Generate ID with the hashcode of the interface class
 */
fun generateId(id: String?, func: Function<*>): String {
    return id?: func::class.hashCode().toString()
}

fun<O> O.apply(apply: (O.() -> Unit)? = null): O {
    if (apply != null) apply(this)

    return this
}


/**
 * Render to external builder
 */
fun<P : Any> Component<P>.renderExternal(data: Data<P>, builder: MessageBuilder) {
    val context = when (builder) {
        is MessageCreateBuilder -> RenderContextCreate(data, this, builder)
        is MessageEditBuilder -> RenderContextEdit(data, this, builder)
        else -> RenderContext(data, builder, this)
    }

    render(context)
}