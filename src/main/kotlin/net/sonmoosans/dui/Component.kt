package net.sonmoosans.dui
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.sonmoosans.dui.listeners.Handler
import net.sonmoosans.dui.listeners.ModalHandler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.utils.apply

typealias MessageBuilder = AbstractMessageBuilder<*, *>

fun<P: Any> component(render: RenderContext<P, *>.() -> Unit) = Component(render = render)

open class Component<P : Any>(
    val store: DataStore<P> = DataStoreImpl(),
    val render: RenderContext<P, *>.() -> Unit
) {
    val listeners = hashMapOf<Int, Handler<*, P>>()
    val modals = hashMapOf<Int, ModalHandler<P>>()

    /**
     * Update Data and renders Component
     *
     * Update only will be invoked if key already exists
     */
    fun update(id: String, update: Data<P>.() -> Unit, default: () -> P): MessageCreateData {
        val data = store[id]?.apply(update)?: Data(default())
        store[id] = data

        return render(id, data)
    }

    /**
     * Store new Data and renders Component
     *
     * Update props If key duplicated
     */
    fun create(id: String, props: P, init: (Data<P>.() -> Unit)? = null): MessageCreateData {
        var cache = store[id]

        if (cache == null) {
            cache = Data(props)
            store[id] = cache
        } else {
            cache.props = props
        }

        return render(id, cache.apply(init))
    }

    /**
     * renders Component
     */
    fun render(id: String, data: Data<P> = store[id]!!): MessageCreateData {
        val context = RenderContextCreate(id, data, this)

        render(context)

        return context.builder.build()
    }

    /**
     * Parse data from id and renders Component
     */
    fun edit(id: String): MessageEditData? {
        val data = store[id] ?: return null

        return edit(id, data)
    }

    /**
     * Parse data from id and renders Component
     */
    fun edit(id: String, data: Data<P>): MessageEditData {
        val context = RenderContextEdit(id, data, this)

        render(context)

        return context.builder.build()
    }
}