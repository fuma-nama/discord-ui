package net.sonmoosans.dui

import net.sonmoosans.dui.context.State

interface DataStore<P> {
    /**
     * Get Props by id
     */
    operator fun get(id: Long): Data<P>?

    operator fun set(key: Long, value: Data<P>)

    fun remove(key: Long)
}

class DataStoreImpl<P> : DataStore<P> {
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
class Data<P>(
    val id: Long,
    var props: P,
    val states: HashMap<String, State<*>> = hashMapOf(),
    val hooks: HashMap<HookKey, Any> = hashMapOf()
)
