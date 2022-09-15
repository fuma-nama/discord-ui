package net.sonmoosans.dui.components

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.Data
import net.sonmoosans.dui.annotations.RequireListener
import net.sonmoosans.dui.annotations.RequireStates
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.scope
import net.sonmoosans.dui.hooks.State
import net.sonmoosans.dui.hooks.useState
import net.sonmoosans.dui.utils.ContainerImpl
import net.sonmoosans.dui.utils.generateId
import net.sonmoosans.dui.utils.value

class Page<D: Data<P>, P : Any>(val render: RenderContext<D, P>.() -> Unit)

class PagesBuilder<D: Data<P>, P : Any> : ContainerImpl<Page<D, P>>() {
    fun page(render: RenderContext<D, P>.() -> Unit) = add(Page(render))
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
fun<D: Data<P>, P : Any> RenderContext<D, P>.pager(
    page: State<Int>? = null,
    scope: String? = null,
    init: PagesBuilder<D, P>.() -> Unit,
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
fun<D: Data<P>, P : Any> RenderContext<D, P>.pager(
    page: Int,
    setPage: (page: Int) -> Unit,
    scope: String? = null,
    init: PagesBuilder<D, P>.() -> Unit,
) = scope(generateId(scope, init)) {
    pager(page, setPage, init)
}

@RequireStates("page")
@RequireListener("prev", "next")
private fun<D: Data<P>, P : Any> RenderContext<D, P>.pager(
    page: Int,
    setPage: (page: Int) -> Unit,
    init: PagesBuilder<D, P>.() -> Unit,
) {
    val pages = PagesBuilder<D, P>().apply(init).list

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

class Tab<D: Data<P>, P : Any>(
    val label: String,
    val description: String?,
    val emoji: Emoji?,
    val page: RenderContext<D, P>.() -> Unit
)

class TabBuilder<D: Data<P>, P : Any>: ContainerImpl<Tab<D, P>>() {
    fun tab(
        label: String,
        description: String? = null,
        emoji: Emoji? = null,
        page: RenderContext<D, P>.() -> Unit
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
fun<D: Data<P>, P : Any> RenderContext<D, P>.tabLayout(
    page: State<Int>? = null,
    scope: String? = null,
    init: TabBuilder<D, P>.() -> Unit,
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
fun<D: Data<P>, P : Any> RenderContext<D, P>.tabLayout(
    page: Int,
    setPage: (page: Int) -> Unit,
    scope: String? = null,
    init: TabBuilder<D, P>.() -> Unit,
) = scope(generateId(scope, init)) {
    tabLayout(page, setPage, init)
}

@RequireListener("change_tab")
@RequireStates("page")
private fun<D: Data<P>, P : Any> RenderContext<D, P>.tabLayout(
    page: Int,
    setPage: (page: Int) -> Unit,
    init: TabBuilder<D, P>.() -> Unit,
) {
    val tabs = TabBuilder<D, P>().apply(init).list
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