package components

import Player
import UnoGame
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.sonmoosans.dui.component
import net.sonmoosans.dui.components.*
import net.sonmoosans.dui.context.EventContext
import net.sonmoosans.dui.once
import net.sonmoosans.dui.utils.Embed
import net.sonmoosans.dui.utils.field
import utils.*
import java.awt.Color

val Dashboard = component<UnoGame> {

    with (props) {
        embed(
            title = "${props.currentPlayer.user.asTag}'s Round!",
            description = "Select Your Card or pick one",
            footer = {
                text = last?.card?.name
            },
            image = {
                url = last?.card?.getImageUrl()
            },
        )

        builder.setEmbeds(builder.embeds + message)
    }

    rowLayout {
        button("Select a Card") {
            val player = props.getPlayer(event) { return@button }

            event.reply(player.action.render()).apply {
                setEphemeral(true)
                queue {
                    player.hook = it
                }
            }
        }

        button("Pick") {
            val current = props.currentPlayer

            checkPlayer(current) { return@button }
            val pick = props.pick(current)

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

val BankBoard = once<List<Player>> {
    embed(
        title = "Game Ended",
        description = "Winners",
    ) {
        fun field(name: String, player: Player) {
            field(name, player.user.asTag, inline = true)
        }

        props.getOrNull(0)?.let {
            field("1st", it)
        }
        props.getOrNull(1)?.let {
            field("2nd", it)
        }
        props.getOrNull(2)?.let {
            field("3rd", it)
        }

        val others = props.drop(3).joinToString { it.user.asTag }
        field("Others", others)
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
