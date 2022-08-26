package utils

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption

class SelectOptionImpl(
    label: String,
    value: String,
    description: String?,
    isDefault: Boolean,
    emoji: Emoji?,
) : SelectOption(
    label, value, description, isDefault, emoji
)

fun SelectMenuInteractionEvent.value() = values.single()!!