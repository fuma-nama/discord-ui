package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.context.RenderContext

fun<C> createContext(): Context<C> {
    return Context()
}

fun<C> RenderContext<*, *>.useContext(context: Context<C>): C {
    return this.contexts?.get(context) as C
}

fun<C> RenderContext<*, *>.useContext(context: Context<C>, initial: C): C {
    return this.contexts?.get(context) as C?
        ?: initial
}

class Context<C> internal constructor()