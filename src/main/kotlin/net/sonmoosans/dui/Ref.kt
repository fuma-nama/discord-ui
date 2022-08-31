package net.sonmoosans.dui

data class Ref<P : Any>(
    val data: Data<P>,
    val comp: Component<P>
) {
    fun render() = comp.render(data)
    fun edit() = comp.edit(data)
    fun destroy() = comp.destroy(data)
}