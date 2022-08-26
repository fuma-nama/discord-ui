package utils

/**
 * Generate ID with the hashcode of the interface class
 */
fun generateId(id: String?, func: () -> Any?): String {
    return id?: func::class.hashCode().toString()
}