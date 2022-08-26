package context

import utils.ContainerImpl

interface Container<C> {
    fun add(element: C)
}

open class RenderContainer<E, P : Any>(val context: RenderContext<P, *>): ContainerImpl<E>()