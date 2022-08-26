package components

import context.Container
import context.RenderContainer
import context.RenderContext
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import utils.join
import utils.lambdaList
import java.util.*

private const val rowSpace = 1.0

/**
 * Detects and split overflowed components into multi Action Rows
 */
fun<P : Any> RenderContext<P, *>.rowLayout(components: RenderContainer<ActionComponent, P>.() -> Unit) {

    val rows = builder.components.join<LayoutComponent>(
        split(lambdaList(components))
    )

    builder.setComponents(rows)
}

/**
 * Detects and split overflowed components into multi Action Rows
 */
fun Container<in ActionRow>.rowLayout(components: Container<ActionComponent>.() -> Unit) {
    for (row in split(lambdaList(components))) {
        add(row)
    }
}

private fun split(components: List<ActionComponent>): List<ActionRow> {
    val rows = arrayListOf<ActionRow>()
    val current: Stack<ActionComponent> = Stack()
    var space = rowSpace

    components.forEach { item ->
        val size = rowSpace / item.type.maxPerRow

        if (size <= space) {
            space -= size
        } else {
            rows += ActionRow.of(current)

            current.clear()
            space = rowSpace
        }

        current.push(item)
    }

    if (current.isNotEmpty())
        rows += ActionRow.of(current)

    return rows
}