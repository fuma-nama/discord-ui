package net.sonmoosans.dui

import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.RenderContext

/**
 * Component that only stores one Data
 */
open class SingleDataComponent<P: Any>(
    initialData: Data<P>? = null,
    render: RenderContext<P, SingleDataComponent<P>>.() -> Unit
): AbstractComponent<P, SingleDataComponent<P>>(render) {
    var data: Data<P>? = initialData

    override fun getData(id: Long): Data<P>? {
        val data = this.data

        return if (data != null && data.id == id) {
            data
        } else null
    }

    /**
     * Set current Data and Return Component itself
     * @param editOnly If enabled, only edit Data props
     */
    fun data(props: P, editOnly: Boolean = true): SingleDataComponent<P> {

        if (editOnly && data != null) {
            data!!.props = props
        } else {
            data = Data(0, props)
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
            this.data = Data(0, data.props)
        }
    }
}