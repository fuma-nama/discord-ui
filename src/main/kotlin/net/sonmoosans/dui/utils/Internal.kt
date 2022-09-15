package net.sonmoosans.dui.utils

import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.MessageBuilder
import net.sonmoosans.dui.context.*

/**
 * Create Hook Key
 */
fun IDScope.createKey(id: String?, func: Function<*>, type: String) =
    createKey(generateId(id, func), type)

/**
 * Create an ID from scope
 */
fun IDScope.createId(id: String?, func: Function<*>) =
    createId(generateId(id, func))

/**
 * Generate ID with the hashcode of the interface class
 */
fun generateId(func: Function<*>): String {
    return func::class.hashCode().toString()
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

interface Delegate<S> {
    var value: S
    operator fun<P> getValue(parent: P, property: Any) = value
    operator fun<P> setValue(parent: P, property: Any, value: S) {
        this.value = value
    }

    operator fun component1() = value
    operator fun component2() = { s: S -> value = s}
    operator fun component3() = { updater: (S) -> S -> value = updater(value)}
}

/**
 * Render to external builder
 */
fun<D: Data<P>, P : Any> Component<D, P>.renderExternal(data: D, builder: MessageBuilder) {
    val context = when (builder) {
        is MessageCreateBuilder -> RenderContextCreate(data, this, builder)
        is MessageEditBuilder -> RenderContextEdit(data, this, builder)
        else -> RenderContextImpl(data, this, builder)
    }

    this.render.invoke(context)
}