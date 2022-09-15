package net.sonmoosans.dui

import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.sonmoosans.dui.context.RenderContext

fun<P: Any> component(render: RenderContext<KeyData<P>, P>.() -> Unit) = IDComponent(render = render)
fun<P: Any> component(store: DataStore<P>, render: RenderContext<KeyData<P>, P>.() -> Unit) = IDComponent(store, render)

/**
 * Component which has no Props required
 */
class NoPropsComponent(
    store: DataStore<Unit> = DataStoreImpl(),
    render: RenderContext<KeyData<Unit>, Unit>.() -> Unit
) : IDComponent<Unit>(store, render) {

    fun create(id: String) = create(id, Unit)
    inline fun create(id: String, init: Data<Unit>.() -> Unit) = create(id, Unit, init)

    /**
     * @see IDComponent.createWithData
     */
    fun createWithData(id: String) = createWithData(id, Unit)
}

open class IDComponent<P : Any>(
    val store: DataStore<P> = DataStoreImpl(),
    render: RenderContext<KeyData<P>, P>.() -> Unit
) : AbstractComponent<KeyData<P>, P, IDComponent<P>>(render) {
    fun getData(id: String) = store[id]

    /**
     * Update Data and renders Component
     */
    fun update(id: String, update: Data<P>.() -> Unit, default: () -> P): MessageCreateData {
        val data = store[id]?: createData(id, default())
        store[id] = data

        return render(data.apply(update))
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     */
    fun create(id: String, props: P): MessageCreateData {
        val data = createData(id, props)

        return render(data)
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     */
    inline fun create(id: String, props: P, init: Data<P>.() -> Unit): MessageCreateData {
        val data = createData(id, props)

        return render(data.apply(init) )
    }

    /**
     * Create A Ref with initial Data
     */
    fun createRef(id: String, props: P): DataRef<P> {
        return DataRef(createData(id, props), this)
    }

    /**
     * Create Data but don't render it
     */
    fun createData(id: String, props: P): KeyData<P> {
        return store.setOrCreate(id, props)
    }

    /**
     * Store new Data and renders Component
     *
     * If key duplicated, Update props and remove its parent
     *
     * @return the data and initial render result
     */
    fun createWithData(id: String, props: P): Pair<Data<P>, MessageCreateData> {
        val data = createData(id, props)

        return data to render(data)
    }

    /**
     * renders Component
     */
    fun render(id: String) = getData(id)?.let { render(it) }

    /**
     * Parse data from id and renders Component
     */
    fun edit(id: String) = getData(id)?.let { edit(it) }

    override fun destroy(data: KeyData<P>) {
        store.remove(data.key)
    }

    override fun parseData(data: String): KeyData<P>? = store[data]

    override fun encodeData(data: KeyData<P>): String {
        return data.key
    }
}

data class DataRef<P : Any>(
    val data: KeyData<P>,
    val comp: IDComponent<P>
) {
    fun render() = comp.render(data)
    fun edit() = comp.edit(data)
    fun destroy() = comp.destroy(data)
}