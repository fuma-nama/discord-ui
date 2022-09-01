package net.sonmoosans.dui

typealias IDRef<P> = Ref<P, in IDComponent<P>>

data class Ref<P : Any, C: Component<P>>(
    val data: Data<P>,
    val comp: C
) {
    fun render() = comp.render(data)
    fun edit() = comp.edit(data)
    fun destroy() = comp.destroy(data)
}