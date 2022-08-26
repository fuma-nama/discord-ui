import context.RenderContext
import context.RenderContextCreate
import context.RenderContextEdit
import listeners.Handler
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditData

typealias MessageBuilder = AbstractMessageBuilder<*, *>

fun<P: Any> component(render: RenderContext<P, *>.() -> Unit) = Component(render = render)

class Component<P : Any>(
    val store: DataStore<P> = DataStoreImpl(),
    val render: RenderContext<P, *>.() -> Unit
) {
    val listeners = hashMapOf<Int, Handler<*, P>>()

    /**
     * Renders Component with temp Data
     */
    fun once(props: P): MessageCreateData {
        val context = RenderContextCreate("", Data(props), this)

        render(context)

        return context.builder.build()
    }

    /**
     * Store new Data and renders Component
     */
    fun create(props: P): MessageCreateData {
        val data = Data(props)
        val id = store.register(data)

        return render(id, data)
    }

    /**
     * renders Component
     */
    fun render(id: String, data: Data<P>): MessageCreateData {
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