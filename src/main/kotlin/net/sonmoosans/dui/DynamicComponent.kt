package net.sonmoosans.dui

import net.sonmoosans.dui.context.RenderContext

interface Generator<P: Any> {
    fun encode(props: P): String
    fun parse(data: String): P
}

fun<P: Any> dynamicComponent(
    generator: Generator<P>,
    render: RenderContext<Data<P>, P>.() -> Unit
) = DynamicComponent(generator, render)

/**
 * Generate a Dynamic ID from custom generator
 *
 * Dynamic Component can save a lot of memory, and don't have memory leak issues.
 *
 * But you must declare and access all variables in props instead of storing them in hooks
 *
 * Ex: Read data from Embed
 */
class DynamicComponent<P: Any>(
    val generator: Generator<P>,
    render: RenderContext<Data<P>, P>.() -> Unit
) : AbstractComponent<Data<P>, P, DynamicComponent<P>>(render) {
    override val dynamic: Boolean = true

    @Deprecated("You can use render instead", replaceWith = ReplaceWith("render(props)"))
    fun create(props: P) = render(props)

    fun render(data: String) = render(parseData(data))
    fun edit(data: String) = edit(parseData(data))

    fun render(props: P) = render(Data(props))
    fun edit(props: P) = edit(Data(props))

    override fun parseData(data: String): Data<P> {
        return Data(generator.parse(data))
    }

    override fun encodeData(data: Data<P>): String {
        return generator.encode(data.props)
    }

    override fun destroy(data: Data<P>) {}
}