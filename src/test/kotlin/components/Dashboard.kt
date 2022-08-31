package components

import Player
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.sonmoosans.dui.component
import net.sonmoosans.dui.components.*
import net.sonmoosans.dui.context.EventContext
import net.sonmoosans.dui.listeners.interaction
import net.sonmoosans.dui.once
import net.sonmoosans.dui.utils.Embed
import net.sonmoosans.dui.utils.toAuthor
import net.sonmoosans.dui.utils.value
import utils.Card
import utils.generateCard
import utils.reply
import java.awt.Color

val Dashboard = component<GameRuntime> {
    val current = props.players[props.current]

    with (props) {
        embed(
            title = "${current.user.asTag}'s Round!",
            description = "Select Your Card or pick one",
            author = last?.owner?.user?.toAuthor(),
            footer = {
                text = last?.card?.name
            },
            image = {
                url = last?.card?.getImageUrl()
            },
        )
    }

    val pickerID = interaction<SelectMenuInteractionEvent, GameRuntime> {
        val selected = event.value().toInt()
        val card = current.cards[selected]

        if (props.put(current, card)) {

            event.ignore()
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

    rowLayout {
        button("Select a Card") {
            checkPlayer(current) { return@button }

            val ui = Picker.create(
                PickerProps(pickerID, props.last?.card, current)
            )

            event.reply(ui).apply {
                setEphemeral(true)
                queue()
            }
        }

        button("Pick") {
            checkPlayer(current) { return@button }
            val pick = generateCard()
            current.cards += pick

            props.next()
            Embed(
                title = "You picked ${pick.name}",
                image = {
                    url = pick.getImageUrl()
                },
                color = Color.GREEN
            ).reply(event) {
                setEphemeral(true)
                queue()
            }
        }
    }
}

data class PickerProps(val pickerID: String, val last: Card?, val player: Player)
val Picker = once<PickerProps> {
    val (pickerID, last, player) = props

    embed(title = "Select a Card")
    row {
        menu(id = pickerID, placeholder = "Select Your card") {

            for ((i, card) in player.cards
                .filter {last == null || it.canPutAbove(last)}
                .withIndex()
            ) {
                option(card.name, i.toString())
            }
        }
    }
}

class GameRuntime(val players: List<Player>, val hook: InteractionHook) {
    val dashboard = Dashboard.createRef(hashCode().toLong(), this)
    var last: Last? = null

    var current: Int = 0

    fun next() {
        if (current >= players.lastIndex) {
            current = 0
        } else {
            current++
        }

        hook.editOriginal(dashboard.edit()).queue()
    }

    fun put(owner: Player, card: Card): Boolean {
        val last = this.last?.card

        return if (last == null || card.canPutAbove(last)) {
            this.last = Last(owner, card)
            next()
            true
        } else {
            false
        }
    }
}

data class Last(val owner: Player, val card: Card)

inline fun EventContext<out IReplyCallback, *>.checkPlayer(current: Player, onFail: () -> Unit) {
    if (event.user != current.user) {
        Embed(
            title = "It's not your round!",
            description = "It is ${current.user.asTag}'s Round",
            color = Color.RED
        ).reply(event) {
            setEphemeral(true)
            queue()
        }

        onFail()
    }
}
