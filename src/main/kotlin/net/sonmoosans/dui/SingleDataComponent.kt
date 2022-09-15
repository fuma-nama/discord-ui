package net.sonmoosans.dui

import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.RenderContext

/**
 * Component that only stores one Data
 */
open class SingleDataComponent<P: Any>(
    initialData: Data<P>? = null,
    render: RenderContext<Data<P>, P>.() -> Unit
): AbstractComponent<Data<P>, P, SingleDataComponent<P>>(render) {
    var data: Data<P>? = initialData

    /**
     * Set current Data and Return Component itself
     * @param editOnly If enabled, only edit Data props
     */
    fun data(props: P, editOnly: Boolean = true): SingleDataComponent<P> {

        if (editOnly && data != null) {
            data!!.props = props
        } else {
            data = Data(props)
        }

        return this
    }

    fun create() = render(data!!)
    fun create(props: P, editOnly: Boolean = true) = data(props, editOnly).render(data!!)

    fun edit() = edit(data!!)
    fun edit(props: P, editOnly: Boolean = true): MessageEditData = data(props, editOnly).edit(data!!)

    override fun destroy(data: Data<P>) {
        if (this.data == data) {
            resetData()
        }
    }

    fun resetData() {
        val data = this.data

         if (data != null) {
            this.data = Data(data.props)
        }
    }

    override fun parseData(data: String): Data<P>? {
        return this.data
    }

    override fun encodeData(data: Data<P>): String {
        return ""
    }
}