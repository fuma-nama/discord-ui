package hooks

import context.RenderContext

fun<C> createContext(): Context<C> {
    return Context()
}

fun<C> RenderContext<*, *>.useContext(context: Context<C>): C {
    return this.contexts[context] as C
}

fun<C> RenderContext<*, *>.useContext(context: Context<C>, initial: C): C {
    return this.contexts[context] as C?
        ?: initial
}

class Context<C> internal constructor()