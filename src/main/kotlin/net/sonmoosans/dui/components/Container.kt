package net.sonmoosans.dui.components

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.sonmoosans.dui.Component
import net.sonmoosans.dui.annotations.RequireListener
import net.sonmoosans.dui.annotations.RequireStates
import net.sonmoosans.dui.context.Container
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.State
import net.sonmoosans.dui.context.scope
import net.sonmoosans.dui.hooks.useState
import net.sonmoosans.dui.utils.ContainerImpl
import net.sonmoosans.dui.utils.generateId
import net.sonmoosans.dui.utils.lambdaList
import net.sonmoosans.dui.utils.value

class Page<P: Any, C: Component<P>>(val render: RenderContext<P, C>.() -> Unit)

class PagesBuilder<P: Any, C: Component<P>> : ContainerImpl<Page<P, C>>() {
    fun page(render: RenderContext<P, C>.() -> Unit) = add(Page(render))
}

/**
 * Generate The ScopeID from init lambda
 * @see pager
 */
@RequireStates("page")
@RequireListener("prev", "next")
fun<P: Any, C: Component<P>> RenderContext<P, C>.pager(
    page: State<Int>? = null,
    init: PagesBuilder<P, C>.() -> Unit,
) = pager(page, generateId(init), init)

/**
 * Pager Adds a Button Row to change current page
 *
 * And render the current page
 *
 * @param scope Scope ID, set to null to directly use root scope
 */
@RequireStates("page")
@RequireListener("prev", "next")
fun<P: Any, C: Component<P>> RenderContext<P, C>.pager(
    page: State<Int>? = null,
    scope: String?,
    init: PagesBuilder<P, C>.() -> Unit,
) = scope(scope) {
    val state = page?: useState("page", 0)
    val current = state.value
    val pages = PagesBuilder<P, C>().apply(init).list

    pages[current].render(this)

    row {
        button("<", disabled = current <= 0, id = "prev") {
            state.value -= 1
        }

        button(">", disabled = current >= pages.lastIndex, id = "next") {
            state.value += 1
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
 * Generate the Scope ID from init lambda
 * @see tabLayout
 */
@RequireListener("change_tab")
@RequireStates("page")
fun<P: Any, C: Component<P>> RenderContext<P, C>.tabLayout(
    page: State<Int>? = null,
    init: TabBuilder<P, C>.() -> Unit,
) = tabLayout(page, generateId(init), init)

/**
 * TabLayout adds a SelectMenu to switch between tabs
 *
 * And renders the current Tab
 *
 * @param scope Scope ID, set to null to directly use root scope
 */
@RequireListener("change_tab")
@RequireStates("page")
fun<P: Any, C: Component<P>> RenderContext<P, C>.tabLayout(
    page: State<Int>? = null,
    scope: String?,
    init: TabBuilder<P, C>.() -> Unit,
) = scope(scope) {
    val state = page?: useState("page", 0)
    val tabs = TabBuilder<P, C>().apply(init).list
    val current = state.value

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

            submit("change_tab") {
                state.value = event.value().toInt()

                event.edit()
            }
        }
    }
}