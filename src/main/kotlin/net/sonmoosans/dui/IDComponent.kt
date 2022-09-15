package net.sonmoosans.dui

import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.sonmoosans.dui.context.RenderContext

fun<P: Any> component(render: RenderContext<P, IDComponent<P>>.() -> Unit) = IDComponent(render = render)
fun<P: Any> component(store: DataStore<P>, render: RenderContext<P, IDComponent<P>>.() -> Unit) = IDComponent(store, render)

/**
 * Component which has no Props required
 */
class NoPropsComponent(
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
    fun createRef(id: Long, props: P): DataRef<P> {
        return DataRef(createData(id, props), this)
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

data class DataRef<P : Any>(
    val data: Data<P>,
    val comp: IDComponent<P>
) {
    fun render() = comp.render(data)
    fun edit() = comp.edit(data)
    fun destroy() = comp.destroy(data)
}