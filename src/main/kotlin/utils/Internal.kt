package utils

/**
 * Generate ID with the hashcode of the interface class
 */
fun generateId(id: String?, func: () -> Any?): String {
    return id?: func::class.hashCode().toString()
}

fun<O> O.apply(apply: (O.() -> Unit)? = null): O {
    if (apply != null) apply(this)

    return this
}