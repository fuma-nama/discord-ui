package net.sonmoosans.dui.utils

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

fun<P : Any, E> RenderContext<P, *>.lambdaList(lambda: RenderContainer<E, P>.() -> Unit): ArrayList<E> {
    return RenderContainer<E, P>(this).apply(lambda).list
}

open class ContainerImpl<E> : Container<E> {
    val list = arrayListOf<E>()

    override fun add(element: E) {
        list.add(element)
    }
}