package components

import Player
import UnoGame
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.sonmoosans.dui.component
import net.sonmoosans.dui.components.*
import net.sonmoosans.dui.context.EventContext
import net.sonmoosans.dui.hooks.useState
import net.sonmoosans.dui.utils.Embed
import net.sonmoosans.dui.utils.value
import utils.BlackCard
import utils.Card
import utils.CardColor
import utils.reply
import java.awt.Color

data class ActionPanelProps(val game: UnoGame, val player: Player)

val ActionPanel = component<ActionPanelProps> {
    var selecting by useState<BlackCard?> { null }.cached()
    val (game, player) = props

    if (selecting != null) {
        embed(title = "Change color")

        row {
            menu(placeholder = "Select a Color") {
                for (color in CardColor.values()) {
                    option(color.name, color.name)
                }

                submit {
                    val current = checkPlayer(game.currentPlayer) { return@submit }

                    val card = selecting!!
                    card.color = CardColor.valueOf(event.value())

                    put(game, current, card) {
                        selecting = null

                        event.edit()
                    }
                }
            }
        }

        return@component
    }

    if (player.cards.isEmpty()) {
        embed(title = "You already won the Game", color = Color.GREEN)

        return@component
    }

    embed(title = "Select a Card")
    rowLayout {
        menu(placeholder = "Select Your card") {
            val last = game.last

            for ((i, card) in player.cards.withIndex()) {
                val available = last == null || card.canPutAbove(last.card)

                option(card.name, i.toString(),
                    emoji = Emoji.fromUnicode(if (available) "✅" else "❌")
                )
            }

            submit {
                val current = checkPlayer(game.currentPlayer) { return@submit }
                val selected = event.value().toInt()

                when (val card = current.cards[selected]) {

                    is BlackCard -> {
                        selecting = card

                        event.edit()
                    }

                    else -> put(game, current, card) {
                        event.edit()
                    }
                }
            }
        }

        button("Update", dynamic = true) {
            event.edit()
        }
    }
}

inline fun<T> EventContext<out T, *, *>.put(game: UnoGame, player: Player, card: Card, then: () -> Unit) where T: IReplyCallback, T: IMessageEditCallback {
    if (game.put(player, card)) {

        if (game.winners.contains(player)) {
            event.editMessageEmbeds(
                Embed(title = "You already won the Game", color = Color.GREEN)
            ).apply {
                isReplace = true
                queue()
            }
        } else {
            then()
        }
    } else {
        Embed(
            title = "You cannot use this Card",
            description = "Try another cards or pick one",
            color = Color.RED
        ).reply(event) {
            setEphemeral(true)
            queue()
        }
    }
}
