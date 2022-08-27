package net.sonmoosans.dui.components

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.State
import net.sonmoosans.dui.utils.ContainerImpl
import net.sonmoosans.dui.utils.value

fun<C: RenderContext<*, *>> C.pager(page: State<Int>, vararg pages: C.() -> Unit) {
    val current = page.value
    pages[current].invoke(this)

    row {
        button("<-", disabled = current <= 0) {
            page.value -= 1
        }

        button("->", disabled = current >= pages.lastIndex) {
            page.value += 1
        }
    }
}

class Tab<C: RenderContext<*, *>>(
    val label: String,
    val description: String?,
    val emoji: Emoji?,
    val page: C.() -> Unit
)

class TabBuilder<C: RenderContext<*, *>>: ContainerImpl<Tab<C>>() {
    fun tab(
        label: String,
        description: String? = null,
        emoji: Emoji? = null,
        page: C.() -> Unit
    ) {
        add(Tab(label, description, emoji, page))
    }
}

fun<C: RenderContext<*, *>> C.tabLayout(page: State<Int>, init: TabBuilder<C>.() -> Unit) {
    val tabs = TabBuilder<C>().apply(init).list
    val current = page.value
    tabs[current].page(this)

    row {
        menu(placeholder = "Select a Tab", selected = current) {
            for ((i, tab) in tabs.withIndex()) {
                option(
                    label = tab.label,
                    description = tab.description,
                    emoji = tab.emoji,
                    value = i.toString()
                )
            }

            submit {
                page *= event.value().toInt()
                event.edit()
            }
        }
    }
}