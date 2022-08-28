package net.sonmoosans.dui.context

import net.sonmoosans.dui.HookKey

interface IDScope {
    var scope: String

    /**
     * Create Hook Key
     */
    fun createKey(id: String, type: String): HookKey {
        return HookKey(createId(id), type)
    }

    /**
     * Create an ID from scope
     */
    fun createId(id: String): String {
        return if (scope.isEmpty()) id
        else "${scope}_$id"
    }
}

open class IDScopeImpl(default: String = "") : IDScope {
    override var scope = default
}

/**
 * Open new ID Scope, used for avoiding ID duplication
 */
inline fun<C: IDScope> C.scope(prefix: String? = null, body: C.() -> Unit) {
    if (prefix == null) {
        return body(this)
    }

    val prev = scope
    scope += "_$prefix"

    body(this)
    scope = prev
}

inline fun rootScope(domain: String = "", body: IDScope.() -> Unit) = IDScopeImpl(domain).apply(body)