package net.sonmoosans.dui

import net.sonmoosans.dui.context.State

interface DataStore<P> {
    /**
     * Get Props by id
     */
    operator fun get(id: String): Data<P>?

    operator fun set(key: String, value: Data<P>)

    fun remove(key: String)
}

class DataStoreImpl<P> : DataStore<P> {
    val map = hashMapOf<String, Data<P>>()

    override fun get(id: String): Data<P>? {
        return map[id]
    }

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun set(key: String, value: Data<P>) {
        map[key] = value
    }
}

data class HookKey(val id: String, val type: String)
class Data<P>(
    var props: P,
    val states: HashMap<String, State<*>> = hashMapOf(),
    val hooks: HashMap<HookKey, Any> = hashMapOf()
)
