package components

import WaitingGame
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.sonmoosans.dui.component
import net.sonmoosans.dui.components.button
import net.sonmoosans.dui.components.embed
import net.sonmoosans.dui.components.row
import net.sonmoosans.dui.utils.Embed
import net.sonmoosans.dui.utils.field
import net.sonmoosans.dui.utils.toAuthor
import utils.reply
import java.awt.Color

val Lobby = component<WaitingGame> {

    embed(
        title = "Waiting for Players",
        description = "${props.players.size}/${props.playersCount} Joined the Game"
    ) {
        for (player in props.players) {
            field(player.name, inline = true)
        }
    }

    row {
        button("Start", style = ButtonStyle.SUCCESS, disabled = !props.canStart, dynamic = true) {

            val game = props.start()

            event.editMessage(game.dashboard.edit()).queue()
        }

        button("Join", dynamic = true) {

            if (props.join(event.user)) {
                Embed(
                    title = "${event.user.asTag} joined the Game",
                    author = event.user.toAuthor(),
                    color = Color.GREEN
                ).reply(event).queue()
            } else {
                Embed(
                    title = "You already Joined the Game",
                    color = Color.RED
                ).reply(event).setEphemeral(true).queue()
            }
        }
        button("Leave", style = ButtonStyle.DANGER, dynamic = true) {
            if (props.leave(event.user)) {

                Embed(
                    title = "${event.user.asTag} left the Game",
                    author = event.user.toAuthor(),
                    color = Color.RED
                ).reply(event).queue()
            } else {
                Embed(
                    title = "You haven't joined the Game yet",
                    color = Color.RED
                ).reply(event).setEphemeral(true).queue()
            }
        }
    }
}