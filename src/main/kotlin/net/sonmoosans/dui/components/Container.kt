package net.sonmoosans.dui.components

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.annotations.RequireListener
import net.sonmoosans.dui.annotations.RequireStates
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.scope
import net.sonmoosans.dui.hooks.State
import net.sonmoosans.dui.hooks.useState
import net.sonmoosans.dui.utils.ContainerImpl
import net.sonmoosans.dui.utils.generateId
import net.sonmoosans.dui.utils.value

class Page<P: Any, C: Component<P>>(val render: RenderContext<P, C>.() -> Unit)

class PagesBuilder<P: Any, C: Component<P>> : ContainerImpl<Page<P, C>>() {
    fun page(render: RenderContext<P, C>.() -> Unit) = add(Page(render))
}

/**
 * Pager Adds a Button Row to change current page
 *
 * And render the current page
 *
 * @param scope Scope ID, generate from lambda if null
 */
@RequireStates("page")
@RequireListener("prev", "next")
fun<P: Any, C: Component<P>> RenderContext<P, C>.pager(
    page: State<Int>? = null,
    scope: String? = null,
    init: PagesBuilder<P, C>.() -> Unit,
) = scope(generateId(scope, init)) {
    val (state, setState) = page?: useState("page", 0)

    return pager(state, setState, init)
}

/**
 * Pager Adds a Button Row to change current page
 *
 * And render the current page
 *
 * @param scope Scope ID, generate from lambda if null
 */
@RequireStates("page")
@RequireListener("prev", "next")
fun<P: Any, C: Component<P>> RenderContext<P, C>.pager(
    page: Int,
    setPage: (page: Int) -> Unit,
    scope: String? = null,
    init: PagesBuilder<P, C>.() -> Unit,
) = scope(generateId(scope, init)) {
    pager(page, setPage, init)
}

@RequireStates("page")
@RequireListener("prev", "next")
private fun<P: Any, C: Component<P>> RenderContext<P, C>.pager(
    page: Int,
    setPage: (page: Int) -> Unit,
    init: PagesBuilder<P, C>.() -> Unit,
) {
    val pages = PagesBuilder<P, C>().apply(init).list

    pages[page].render(this)

    row {
        button("<", disabled = page <= 0, id = "prev") {
            setPage(page - 1)
        }

        button(">", disabled = page >= pages.lastIndex, id = "next") {
            setPage(page + 1)
        }
    }
}

class Tab<P: Any, C: Component<P>>(
    val label: String,
    val description: String?,
    val emoji: Emoji?,
    val page: RenderContext<P, C>.() -> Unit
)

class TabBuilder<P: Any, C: Component<P>>: ContainerImpl<Tab<P, C>>() {
    fun tab(
        label: String,
        description: String? = null,
        emoji: Emoji? = null,
        page: RenderContext<P, C>.() -> Unit
    ) {
        add(Tab(label, description, emoji, page))
    }
}

/**
 * TabLayout adds a SelectMenu to switch between tabs
 *
 * And renders the current Tab
 *
 * @param scope Scope ID, generate from lambda if null
 */
@RequireListener("change_tab")
@RequireStates("page")
fun<P: Any, C: Component<P>> RenderContext<P, C>.tabLayout(
    page: State<Int>? = null,
    scope: String? = null,
    init: TabBuilder<P, C>.() -> Unit,
) = scope(generateId(scope, init)) {
    val (state, setState) = page?: useState("page", 0)

    tabLayout(state, setState, init)
}

/**
 * TabLayout adds a SelectMenu to switch between tabs
 *
 * And renders the current Tab
 *
 * @param scope Scope ID, generate from lambda if null
 */
@RequireListener("change_tab")
@RequireStates("page")
fun<P: Any, C: Component<P>> RenderContext<P, C>.tabLayout(
    page: Int,
    setPage: (page: Int) -> Unit,
    scope: String? = null,
    init: TabBuilder<P, C>.() -> Unit,
) = scope(generateId(scope, init)) {
    tabLayout(page, setPage, init)
}

@RequireListener("change_tab")
@RequireStates("page")
private fun<P: Any, C: Component<P>> RenderContext<P, C>.tabLayout(
    page: Int,
    setPage: (page: Int) -> Unit,
    init: TabBuilder<P, C>.() -> Unit,
) {
    val tabs = TabBuilder<P, C>().apply(init).list
    tabs[page].page(this)

    row {
        menu(placeholder = "Select a Tab", selected = page) {
            for ((i, tab) in tabs.withIndex()) {
                option(
                    label = tab.label,
                    description = tab.description,
                    emoji = tab.emoji,
                    value = i.toString()
                )
            }

            submit("change_tab") {
                setPage(event.value().toInt())

                event.edit()
            }
        }
    }
}