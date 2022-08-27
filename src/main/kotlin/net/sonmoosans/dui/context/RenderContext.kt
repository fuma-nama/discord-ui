package net.sonmoosans.dui.context

import net.sonmoosans.dui.Data
import net.sonmoosans.dui.hooks.Context
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.MessageBuilder
import net.sonmoosans.dui.ParentData
import net.sonmoosans.dui.utils.renderExternal

@DslMarker
annotation class DslBuilder

class RenderContextEdit<P: Any>(
    data: Data<P>,
    component: Component<P>,
    builder: MessageEditBuilder = MessageEditBuilder()
) :
    RenderContext<P, MessageEditBuilder>(
        data, builder, component
    )

class RenderContextCreate<P: Any>(
    data: Data<P>,
    component: Component<P>,
    builder: MessageCreateBuilder = MessageCreateBuilder()
) :
    RenderContext<P, MessageCreateBuilder>(
        data, builder, component
    )

open class RenderContext<P: Any, B: MessageBuilder>(
    data: Data<P>,
    val builder: B,
    component: Component<P>
): DataContext<P>(data, component) {
    var contexts: Map<Context<*>, Any?>? = null

    /**
     * Run lambda if render mode is edit
     */
    inline fun onEdit(run: RenderContextEdit<P>.() -> Unit) {
        if (this is RenderContextEdit) {
            run(this as RenderContextEdit<P>)
        }
    }

    /**
     * Run lambda if render mode is create
     */
    inline fun onCreate(run: RenderContextCreate<P>.() -> Unit) {
        if (this is RenderContextCreate) {
            run(this as RenderContextCreate<P>)
        }
    }

    operator fun<P: Any> Component<P>.invoke(id: Long, props: P) {
        val data = store.setOrCreate(id, props)
        data.parent = ParentData(
            this@RenderContext.component,
            this@RenderContext.data.id
        )

        renderExternal(data, builder)
    }

    fun<C : Any> Context<C>.provider(value: C, children: RenderContext<P, B>.() -> Unit) {
        val prev = contexts

        contexts = HashMap(prev).also {
            it[this] = value
        }

        children(this@RenderContext)

        contexts = prev
    }
}

class State<S>(
    val id: String,
    /**
     * Never access this variable directly
     */
    internal var raw: S
)
