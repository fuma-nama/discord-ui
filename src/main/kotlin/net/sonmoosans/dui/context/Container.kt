package net.sonmoosans.dui.context

import net.sonmoosans.dui.utils.ContainerImpl

interface Container<C> {
    fun add(element: C)
}

open class RenderContainer<E, P : Any>(val context: RenderContext<P, *>): ContainerImpl<E>()