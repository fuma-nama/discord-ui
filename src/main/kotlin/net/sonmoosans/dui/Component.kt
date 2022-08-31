package net.sonmoosans.dui

import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.sonmoosans.dui.listeners.Handler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.EventContext

typealias MessageBuilder = AbstractMessageBuilder<*, *>

fun<P: Any> component(render: RenderContext<P, *>.() -> Unit) = IDComponent(render = render)
fun<P: Any> component(store: DataStore<P>, render: RenderContext<P, *>.() -> Unit) = IDComponent(store, render)

/**
 * Component which has no Data required
 */
class NoDataComponent(
    store: DataStore<Unit> = DataStoreImpl(),
    render: RenderContext<Unit, *>.() -> Unit
) : IDComponent<Unit>(store, render) {

    fun create(id: Long) = create(id, Unit)
    inline fun create(id: Long, init: Data<Unit>.() -> Unit) = create(id, Unit, init)
    fun initData(id: Long) = initData(id, Unit)
}

/**
 * Component which has no Data required
 */
class SingleNoDataComponent(
    render: RenderContext<Unit, *>.() -> Unit
) : SingleDataComponent<Unit>(Data(0, Unit), render)

/**
 * Component that only stores one Data
 */
open class SingleDataComponent<P: Any>(
    initialData: Data<P>? = null,
    override val render: RenderContext<P, *>.() -> Unit
): AbstractComponent<P>() {
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

        this.data = if (data != null) {
            Data(0, data.props)
        } else {
            null
        }
    }
}

open class IDComponent<P : Any>(
    val store: DataStore<P> = DataStoreImpl(),
    override val render: RenderContext<P, *>.() -> Unit
) : AbstractComponent<P>() {

    override fun getData(id: Long) = store[id]

    /**
     * Update Data and renders Component
     */
    fun update(id: Long, update: Data<P>.() -> Unit, default: () -> P): MessageCreateData {
        val data = store[id]?: Data(id, default())
        store[id] = data

        return render(data.apply(update))
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     */
    fun create(id: Long, props: P): MessageCreateData {
        val data = store.setOrCreate(id, props)

        return render(data)
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     *
     * @return the data and initial render result
     */
    fun initData(id: Long, props: P): Pair<Data<P>, MessageCreateData> {
        val data = store.setOrCreate(id, props)

        return data to render(data)
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     */
    inline fun create(id: Long, props: P, init: Data<P>.() -> Unit): MessageCreateData {
        val data = store.setOrCreate(id, props)

        return render(data.apply(init))
    }

    /**
     * renders Component
     */
    fun render(id: Long) = store[id]?.let { render(it) }

    /**
     * Parse data from id and renders Component
     */
    fun edit(id: Long) = store[id]?.let { edit(it) }

    override fun destroy(data: Data<P>) {
        store.remove(data.id)
    }
}

abstract class AbstractComponent<P: Any> : Component<P> {
    override val listeners = hashMapOf<String, Handler<EventContext<*, P>>>()
    var export: Any? = null

    override fun listen(id: String, listener: Handler<EventContext<*, P>>): String {
        listeners[id] = listener

        return id
    }

    /**
     * Read Exported variable
     */
    fun<T> readForce(): T {
        return export as T
    }

    /**
     * Read Exported variable
     */
    inline fun<reified T> read(): T {
        return export as T
    }
}

interface Component<P : Any> {
    val listeners: Map<String, Handler<EventContext<*, P>>>
    val render: RenderContext<P, *>.() -> Unit

    fun getData(id: Long): Data<P>?

    fun listen(id: String, listener: Handler<EventContext<*, P>>): String

    /**
     * renders Component
     */
    fun render(data: Data<P>): MessageCreateData {
        val context = RenderContextCreate(data, this)

        render(context)

        return context.builder.build()
    }

    /**
     * Parse data from id and renders Component
     */
    fun edit(data: Data<P>): MessageEditData {
        val context = RenderContextEdit(data, this)

        render(context)

        return context.builder.build()
    }

    fun destroy(data: Data<P>)
}