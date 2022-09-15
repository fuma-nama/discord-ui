package net.sonmoosans.dui.utils

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.context.Container
import net.sonmoosans.dui.context.RenderContainer
import net.sonmoosans.dui.context.RenderContext

fun<E> Collection<E>.join(element: E): ArrayList<E> {
    val result = ArrayList(this)

    result.add(element)
    return result
}

fun<E> Collection<E>.join(element: Collection<E>): ArrayList<E> {
    val result = ArrayList(this)

    result.addAll(element)
    return result
}

fun<E> lambdaList(lambda: Container<E>.() -> Unit): ArrayList<E> {
    return ContainerImpl<E>().apply(lambda).list
}

fun<D: Data<P>, P : Any, E> RenderContext<D, P>.lambdaList(lambda: RenderContainer<E, D, P>.() -> Unit): ArrayList<E> {
    return RenderContainer<E, D, P>(this).apply(lambda).list
}

open class ContainerImpl<E> : Container<E> {
    val list = arrayListOf<E>()

    override fun add(element: E) {
        list.add(element)
    }
}