import components.RankBoard
import components.Dashboard
import components.Last
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.sonmoosans.dui.components.button
import net.sonmoosans.dui.components.row
import net.sonmoosans.dui.once
import net.sonmoosans.dui.utils.Embed
import net.sonmoosans.dui.utils.toAuthor
import utils.*
import java.awt.Color

val a = once<Unit> {
    row {
        button("") {
        }
    }
}
class UnoGame(val players: ArrayList<Player>, val hook: InteractionHook) {
    init {
        for (player in players) {
            player.init(this)
        }
    }

    val winners = arrayListOf<Player>()

    val dashboard = Dashboard.createRef(hashCode().toString(), this)
    var last: Last? = null

    var current: Int = 0
    val currentPlayer get() = players[current]
    var reversed = false
    var message = arrayListOf<MessageEmbed>()

    inline fun getPlayer(event: IReplyCallback, user: User = event.user, onNull: () -> Unit): Player {
        val player = players.find { it.user == user }

        if (player == null) {
            event.replyEmbeds(Embed(
                title = "You must join the game"
            )).setEphemeral(true).queue()

            onNull()
        }
        return player!!
    }

    fun plus(player: Player, count: Int) {
        println("${player.user.name} $count")
        message += Embed(
            title = "Gave $count cards to ${player.user.name}"
        )

        player.cards += generateCards(count)
    }

    fun pick(player: Player): Card {
        this.message.clear()
        val pick = generateCard()
        player.cards += pick

        message += Embed(
            title = "${player.user.asTag} Picked a Card"
        )

        updateHook(nextPlayer)
        return pick
    }

    fun put(owner: Player, card: Card): Boolean {
        val last = this.last?.card

        return if (last == null || card.canPutAbove(last)) {
            this.last = Last(owner, card)
            this.message.clear()
            owner.cards.remove(card)

            message += Embed(
                title = "${owner.user.asTag} used ${card.name}",
                author = owner.user.toAuthor()
            )

            val next = players[nextPlayer]

            when (card) {
                is BlackCard -> {
                    when (card.function) {
                        BlackCardFunction.Plus4 -> plus(next, 4)
                        else -> {}
                    }

                    message += Embed(
                        title = "Now color is changed to ${card.color}"
                    )
                }
                is FunctionCard -> {
                    when (card.function) {
                        CardFunction.Skip -> {
                            message += Embed(
                                title = "${next.user.name}'s round is Skipped!"
                            )

                            current = nextPlayer
                        }
                        CardFunction.Plus2 -> plus(next, 2)
                        CardFunction.Reverse -> {
                            message += Embed(
                                title = "Reversed!"
                            )

                            reversed = true
                        }
                    }
                }
            }

            if (owner.cards.isEmpty()) {
                players -= owner
                winners += owner

                message += Embed(
                    title = "${owner.user.name} Win the Game",
                    color = Color.GREEN
                )
            } else if (owner.cards.size == 1) {
                message += Embed(
                    title = "${owner.user.name} Shouted 'UNO!'"
                )
            }

            if (players.size < 2) {
                end()
            } else {
                updateHook(nextPlayer)
            }

            true
        } else {
            false
        }
    }

    fun end() {
        dashboard.destroy()
        for (player in players) {
            player.action.destroy()
        }

        hook.editOriginal(RankBoard.edit(winners)).queue()
    }

    private fun updateHook(current: Int) {
        this.current = current

        currentPlayer.apply {
            hook?.editOriginal(
                action.edit()
            )?.queue()
        }
        hook.editOriginal(dashboard.edit()).queue()
    }

    private val nextPlayer get(): Int {
        return if (reversed) {
            if (current <= 0) {
                players.lastIndex
            } else {
                current - 1
            }
        } else {
            if (current >= players.lastIndex) {
                0
            } else {
                current + 1
            }
        }
    }
}