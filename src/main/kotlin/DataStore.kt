import context.State


interface DataStore<P> {
    /**
     * @return Props ID
     */
    fun register(data: Data<P>): String

    /**
     * Get Props by id
     */
    operator fun get(id: String): Data<P>?

    operator fun set(key: String, value: Data<P>)

    fun remove(key: String)
}

class DataStoreImpl<P> : DataStore<P> {
    var nextId = 0
    val map = hashMapOf<Int, Data<P>>()

    override fun register(data: Data<P>): String {
        val id = nextId++
        map[id] = data

        return id.toString()
    }

    override fun get(id: String): Data<P>? {
        return map[id.toInt()]
    }

    override fun remove(key: String) {
        map.remove(key.toInt())
    }

    override fun set(key: String, value: Data<P>) {
        map[key.toInt()] = value
    }
}

data class HookKey(val id: String, val type: String)
class Data<P>(
    var props: P,
    val states: HashMap<String, State<*>> = hashMapOf(),
    val hooks: HashMap<HookKey, Any> = hashMapOf()
)
