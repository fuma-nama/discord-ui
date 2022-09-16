package net.sonmoosans.dui

import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.RenderContext

fun<P: Any> once(render: RenderContext<Data<P>, P>.() -> Unit) = OnceComponent(render)

/**
 * Similar to SinceDataComponent, but data won't be stored at every render
 *
 * since no data is stored, you must use Dynamic Listeners and pass an initial data
 */
class OnceComponent<P: Any>(render: RenderContext<Data<P>, P>.() -> Unit) : AbstractComponent<Data<P>, P, OnceComponent<P>>(render) {
    override val dynamic: Boolean = true

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

    private fun createData(props: P) = Data(props)

    override fun destroy(data: Data<P>) {}
    override fun parseData(data: String): Data<P>? {
        return null
    }

    override fun encodeData(data: Data<P>): String {
        return ""
    }
}