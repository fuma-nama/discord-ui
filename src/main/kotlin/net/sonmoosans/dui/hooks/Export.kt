package net.sonmoosans.dui.hooks

import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.HookKey
import net.sonmoosans.dui.SingleDataComponent
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
fun<D> Data<*>.import(id: String = "default", vararg scopes: String): D {
    val key = HookKey(buildId(* scopes, id), "useExport")

    return hooks[key] as D
}

/**
 * Read Exported data from the component
 */
fun<D> Component<*>.import(dataId: Long, id: String = "default"): D? {
    return getData(dataId)?.import(id)
}

/**
 * Read Exported data from the component
 */
fun<D> SingleDataComponent<*>.import(id: String = "default"): D {
    return data!!.import(id)
}