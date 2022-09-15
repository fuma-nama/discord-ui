package net.sonmoosans.dui.context

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.utils.ContainerImpl

interface Container<C> {
    fun add(element: C)
}

open class RenderContainer<E, D: Data<P>, P : Any>(val context: RenderContext<D, P>): ContainerImpl<E>()