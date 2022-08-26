package context

import Component
import Data

@DslBuilder
open class DataContext<P : Any>(
    val id: String,
    final override val data: Data<P>,
    val component: Component<P>
): StateContext<P> {
    val props by data::props

    fun destroy() {
        component.store.remove(id)
    }
}

interface StateContext<P> {
    val data: Data<P>

    val<S> State<S>.current
        get() = data.states[id]!! as State<S>

    var <S> State<S>.value: S
        get() = current.raw
        set(v) {
            current.raw = v
        }

    infix fun<S> State<S>.update(updater: S.() -> Unit) {
        updater(value)
    }

    operator fun<S> State<S>.timesAssign(value: S) { this.value = value }

    infix fun<S> State<S>.set(value: (prev: S) -> S) {
        this.value = value(this.value)
    }

    fun<S> State<S>.asString() = raw.toString()
}