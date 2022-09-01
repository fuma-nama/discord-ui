package net.sonmoosans.dui

import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.sonmoosans.dui.listeners.Handler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.EventContext

typealias Element<P> = RenderContext<P, out Component<P>>
typealias MessageBuilder = AbstractMessageBuilder<*, *>

fun<P: Any> component(render: RenderContext<P, IDComponent<P>>.() -> Unit) = IDComponent(render = render)
fun<P: Any> component(store: DataStore<P>, render: RenderContext<P, IDComponent<P>>.() -> Unit) = IDComponent(store, render)

/**
 * Component which has no Data required
 */
class NoDataComponent(
    store: DataStore<Unit> = DataStoreImpl(),
    render: RenderContext<Unit, *>.() -> Unit
) : IDComponent<Unit>(store, render) {

    fun create(id: Long) = create(id, Unit)
    inline fun create(id: Long, init: Data<Unit>.() -> Unit) = create(id, Unit, init)

    /**
     * @see IDComponent.createWithData
     */
    fun createWithData(id: Long) = createWithData(id, Unit)
}

open class IDComponent<P : Any>(
    val store: DataStore<P> = DataStoreImpl(),
    render: RenderContext<P, IDComponent<P>>.() -> Unit
) : AbstractComponent<P, IDComponent<P>>(render) {
    override fun getData(id: Long) = store[id]

    /**
     * Update Data and renders Component
     */
    fun update(id: Long, update: Data<P>.() -> Unit, default: () -> P): MessageCreateData {
        val data = store[id]?: createData(id, default())
        store[id] = data

        return render(data.apply(update))
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     */
    fun create(id: Long, props: P): MessageCreateData {
        val data = createData(id, props)

        return render(data)
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     */
    inline fun create(id: Long, props: P, init: Data<P>.() -> Unit): MessageCreateData {
        val data = createData(id, props)

        return render(data.apply(init) )
    }

    /**
     * Create A Ref with initial Data
     */
    fun createRef(id: Long, props: P): IDRef<P> {
        return Ref(createData(id, props), this)
    }

    /**
     * Create Data but don't render it
     */
    fun createData(id: Long, props: P): Data<P> {
        return store.setOrCreate(id, props)
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     *
     * @return the data and initial render result
     */
    fun createWithData(id: Long, props: P): Pair<Data<P>, MessageCreateData> {
        val data = createData(id, props)

        return data to render(data)
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

abstract class AbstractComponent<P: Any, C: AbstractComponent<P, C>>(
    render: RenderContext<P, C>.() -> Unit
) : Component<P> {
    override val render = render as RenderContext<P, Component<P>>.() -> Unit
    override val listeners = hashMapOf<String, Handler<EventContext<*, out Component<P>, P>>>()

    override fun listen(id: String, listener: Handler<EventContext<*, out Component<P>, P>>): String {
        listeners[id] = listener

        return id
    }
}

interface Component<P : Any> {
    val listeners: Map<String, Handler<EventContext<*, *, P>>>
    val render: RenderContext<P, Component<P>>.() -> Unit

    fun getData(id: Long): Data<P>?

    fun listen(id: String, listener: Handler<EventContext<*, out Component<P>, P>>): String

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