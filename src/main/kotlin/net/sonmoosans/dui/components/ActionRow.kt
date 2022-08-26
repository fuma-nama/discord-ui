package net.sonmoosans.dui.components

import net.sonmoosans.dui.listeners.Handler
import net.sonmoosans.dui.listeners.interaction
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl
import net.dv8tion.jda.internal.interactions.component.SelectMenuImpl
import net.dv8tion.jda.internal.interactions.component.TextInputImpl
import net.sonmoosans.dui.context.*
import net.sonmoosans.dui.utils.SelectOptionImpl
import net.sonmoosans.dui.utils.join
import net.sonmoosans.dui.utils.lambdaList

fun<P : Any> RenderContext<P, *>.row(components: RenderContainer<ActionComponent, P>.() -> Unit) {

    val rows = builder.components.join<LayoutComponent>(
        ActionRow.of(lambdaList(components))
    )

    builder.setComponents(rows)
}

fun Container<in ActionRow>.row(components: Container<ActionComponent>.() -> Unit) {

    add(
        ActionRow.of(lambdaList(components))
    )
}

fun Container<in Button>.button(
    label: String,
    id: String? = null,
    url: String? = null,
    disabled: Boolean = false,
    emoji: Emoji? = null,
    style: ButtonStyle? = if (url != null) ButtonStyle.LINK else ButtonStyle.PRIMARY,
) = add(
    ButtonImpl(id, label, style, url, disabled, emoji)
)

fun<P: Any> RenderContainer<in Button, P>.button(
    label: String,
    disabled: Boolean = false,
    emoji: Emoji? = null,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    onClick: InteractionContext<ButtonInteractionEvent, P>.() -> Unit,
) {
    val id = context.interaction(onClick)

    add(
        ButtonImpl(id, label, style, null, disabled, emoji)
    )
}

fun Container<in SelectMenu>.menu(
    id: String,
    placeholder: String? = null,
    minValues: Int = 1,
    maxValues: Int = 1,
    disabled: Boolean = false,
    selected: Any? = null,
    init: Container<SelectOption>.() -> Unit
) {
    var options: List<SelectOption> = lambdaList(init)

    if (selected != null) {
        options = options.map {

            it.withDefault(it.value == selected.toString())
        }
    }

    add(
        SelectMenuImpl(id, placeholder, minValues, maxValues, disabled, options)
    )
}

fun<P: Any> RenderContainer<in SelectMenu, P>.menu(
    placeholder: String? = null,
    minValues: Int = 1,
    maxValues: Int = 1,
    disabled: Boolean = false,
    selected: Any? = null,
    init: MenuBuilder<P>.() -> Unit
) {
    val menu = MenuBuilder(context).apply(init)
    var options: List<SelectOption> = menu.list

    if (selected != null) {
        options = options.map {

            it.withDefault(it.value == selected.toString())
        }
    }

    add(
        SelectMenuImpl(menu.id, placeholder, minValues, maxValues, disabled, options)
    )
}

fun Container<in SelectOption>.option(
    label: String,
    value: String,
    description: String? = null,
    selected: Boolean = false,
    emoji: Emoji? = null,
) {
    add(
        SelectOptionImpl(
        label, value, description, selected, emoji
    )
    )
}

class MenuBuilder<P: Any>(context: RenderContext<P, *>): RenderContainer<SelectOption, P>(context) {
    lateinit var id: String

    fun submit(onSubmit: Handler<InteractionContext<SelectMenuInteractionEvent, P>>): String {
        id = context.interaction(onSubmit)

        return id
    }
}

fun Container<in TextInput>.input(
    id: String,
    label: String,
    style: TextInputStyle = TextInputStyle.SHORT,
    minLength: Int = -1,
    maxLength: Int = -1,
    required: Boolean = true,
    value: String? = null,
    placeholder: String? = null
): TextInputImpl {
    val input = TextInputImpl(
        id,
        style,
        label,
        minLength,
        maxLength,
        required,
        value,
        placeholder
    )

    add(input)
    return input
}