package net.sonmoosans.dui

import net.sonmoosans.dui.context.EventContext
import net.sonmoosans.dui.listeners.Handler

interface DataStore<P : Any> {
    /**
     * Get Props by id
     */
    operator fun get(id: String): KeyData<P>?

    operator fun set(key: String, value: KeyData<P>)

    fun remove(key: String)

    /**
     * If data exists, update its props
     *
     * Otherwise, create a new one
     */
    fun setOrCreate(id: String, props: P): KeyData<P> {
        val data = this[id]?.also {
            it.props = props
        } ?: KeyData(id, props).also {
            this[id] = it
        }

        return data
    }
}

class DataStoreImpl<P : Any> : DataStore<P> {
    val map = hashMapOf<String, KeyData<P>>()

    override fun get(id: String): KeyData<P>? {
        return map[id]
    }

    override fun set(key: String, value: KeyData<P>) {
        map[key] = value
    }

    override fun remove(key: String) {
        map.remove(key)
    }
}

data class HookKey(val id: String, val type: String)

class KeyData<P: Any>(val key: String, props: P) : Data<P>(props)

open class Data<P : Any>(
    var props: P
) {
    val listeners by lazy { hashMapOf<String, Handler<EventContext<*, Data<P>, P>>>() }
    val hooks by lazy { hashMapOf<HookKey, Any?>() }
}
