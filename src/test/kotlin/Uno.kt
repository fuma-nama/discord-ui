import components.GameRuntime
import components.Lobby
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.InteractionHook
import net.sonmoosans.bjda.plugins.supercommand.SuperCommandGroup
import net.sonmoosans.bjda.plugins.supercommand.builder.command
import net.sonmoosans.dui.utils.Embed
import utils.Card
import utils.generateCards
import java.awt.Color

val UnoCommands = SuperCommandGroup.create("uno", "Game Commands") {
    command(start())
}

fun start() = command("start", "Start new Game") {
    val maxPlayer = int("player_count", "Game Player count")
        .default { 4 }

    execute {
        WaitingGame(maxPlayer()).apply {
            hook = event.hook
            join(event.user)

            event.reply(lobby.render()).queue()
        }
    }
}

class WaitingGame(
    val playersCount: Int,
) {
    lateinit var hook: InteractionHook
    val lobby = Lobby.createRef(hashCode().toLong(), this)
    val players = hashSetOf<User>()

    val canStart: Boolean
        get() = players.size >= playersCount

    fun start(): GameRuntime {
        lobby.destroy()
        val players = this.players.map {
            Player(it, generateCards(10))
        }
        return GameRuntime(players.toList(), hook)
    }

    fun join(user: User): Boolean {
        if (players.contains(user)) return false

        players += user
        hook.editOriginal(lobby.edit()).queue()

        return true
    }

    fun leave(user: User): Boolean {
        val removed = players.remove(user)

        if (players.isEmpty()) {

            hook.editOriginalEmbeds(Embed(
                title = "Game Ended",
                description = "All Players left the Game",
                color = Color.RED
            )).apply {
                isReplace = true
                queue()
            }
        }

        return removed
    }
}

class Player(
    val user: User,
    val cards: ArrayList<Card>
)
