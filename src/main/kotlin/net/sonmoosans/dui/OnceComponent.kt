package net.sonmoosans.dui

import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.RenderContext

fun<P: Any> once(render: RenderContext<P, OnceComponent<P>>.() -> Unit) = OnceComponent(render)

/**
 * Similar to SinceDataComponent, but data won't be stored at every render
 *
 * Listeners won't work since no data is stored
 */
class OnceComponent<P: Any>(render: RenderContext<P, OnceComponent<P>>.() -> Unit) : AbstractComponent<P, OnceComponent<P>>(render) {
    override fun getData(id: Long): Data<P>? {
        return null
    }

    fun create(props: P) = render(createData(props))
    fun edit(props: P) = edit(createData(props))

    fun createWithData(props: P): Pair<Data<P>, MessageCreateData> {
        val data = createData(props)
        return data to render(data)
    }

    fun editWithData(props: P): Pair<Data<P>, MessageEditData> {
        val data = createData(props)
        return data to edit(data)
    }

    private fun createData(props: P) = Data(0, props)

    override fun destroy(data: Data<P>) {}
}