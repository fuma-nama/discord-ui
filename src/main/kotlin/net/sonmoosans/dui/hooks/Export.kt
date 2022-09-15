package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.*
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.buildId

/**
 * Export data from the component
 */
fun RenderContext<*, *>.useExport(id: String = "default", data: Any) {
    val key = createKey(id, "useExport")

    this.data.hooks[key] = data
}

/**
 * Read Exported data from the component
 */
fun<D> DataRef<*>.import(id: String = "default", vararg scopes: String) = data.import<D>(id, * scopes)

/**
 * Read Exported data from the component
 */
fun<D> Data<*>.import(id: String = "default", vararg scopes: String): D {
    val key = HookKey(buildId(* scopes, id), "useExport")

    return hooks[key] as D
}
/**
 * Read Exported data from the component
 */
fun<D> IDComponent<*>.import(key: String, id: String = "default", vararg scopes: String): D? {
    return getData(key)?.import(id, *scopes)
}

/**
 * Read Exported data from the component
 */
fun<D> Component<*, *>.import(data: String, id: String = "default", vararg scopes: String): D? {
    return parseData(data)?.import(id, *scopes)
}

/**
 * Read Exported data from the component
 */
fun<D> SingleDataComponent<*>.import(id: String = "default", vararg scopes: String): D {
    return data!!.import(id, *scopes)
}