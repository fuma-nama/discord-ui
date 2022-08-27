package net.sonmoosans.dui
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.sonmoosans.dui.listeners.Handler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData
import net.sonmoosans.dui.context.EventContext
import net.sonmoosans.dui.utils.apply

typealias MessageBuilder = AbstractMessageBuilder<*, *>

fun<P: Any> component(render: RenderContext<P, *>.() -> Unit) = Component(render = render)

open class Component<P : Any>(
    val store: DataStore<P> = DataStoreImpl(),
    val render: RenderContext<P, *>.() -> Unit
) {
    val listeners = hashMapOf<Int, Handler<EventContext<*, P>>>()

    fun listen(listener: Handler<EventContext<*, P>>): Int {
        val id = listener::class.hashCode()
        listeners[id] = listener
        return id
    }

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
    fun create(id: Long, props: P, init: (Data<P>.() -> Unit)? = null): MessageCreateData {
        val data = store.setOrCreate(id, props)

        return render(data.apply(init))
    }

    /**
     * renders Component
     */
    fun render(id: Long) = store[id]?.let { render(it) }

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
    fun edit(id: Long) = store[id]?.let { edit(it) }

    /**
     * Parse data from id and renders Component
     */
    fun edit(data: Data<P>): MessageEditData {
        val context = RenderContextEdit(data, this)

        render(context)

        return context.builder.build()
    }
}