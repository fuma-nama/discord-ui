package net.sonmoosans.dui.context

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.utils.ContainerImpl

interface Container<C> {
    fun add(element: C)
}

open class RenderContainer<E, C: Component<P>, P : Any>(val context: RenderContext<P, C>): ContainerImpl<E>()