package net.sonmoosans.dui

import net.sonmoosans.dui.context.EventContext
import net.sonmoosans.dui.listeners.Handler

interface DataStore<P : Any> {
    /**
     * Get Props by id
     */
    operator fun get(id: Long): Data<P>?

    operator fun set(key: Long, value: Data<P>)

    fun remove(key: Long)

    /**
     * If data exists, update its props
     *
     * Otherwise, create a new one
     */
    fun setOrCreate(id: Long, props: P): Data<P> {
        val data = this[id]?.also {
            it.props = props
        } ?: Data(id, props).also {
            this[id] = it
        }

        return data
    }
}

class DataStoreImpl<P : Any> : DataStore<P> {
    val map = hashMapOf<Long, Data<P>>()

    override fun get(id: Long): Data<P>? {
        return map[id]
    }

    override fun set(key: Long, value: Data<P>) {
        map[key] = value
    }

    override fun remove(key: Long) {
        map.remove(key)
    }
}

data class HookKey(val id: String, val type: String)

class Data<P : Any>(
    val id: Long,
    var props: P
) {
    val listeners by lazy { hashMapOf<String, Handler<EventContext<*, *, P>>>() }
    val hooks by lazy { hashMapOf<HookKey, Any>() }
}
