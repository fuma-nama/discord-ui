package utils

fun generateCards(count: Int): ArrayList<Card> {
    val cards = arrayListOf<Card>()

    for (i in 0..count) {
        cards += generateCard()
    }
    return cards
}

fun generateCard(): Card {
    val result = (0..10).random()

    return if (result <= 5) {
        NumberCard(
            CardColor.values().random(),
            (1..9).random()
        )
    } else if (result <= 8) {
        FunctionCard(
            CardColor.values().random(),
            CardFunction.values().random()
        )
    } else {
        BlackCard(
            BlackCardFunction.values().random()
        )
    }
}

class NumberCard(
    override val color: CardColor,
    val number: Int
) : ColorCard(number.toString()) {
    override val name = "${color.name} $number"

    override fun canPutAbove(other: Card): Boolean {
        return when (other) {
            is NumberCard -> other.color == color || other.number == number
            is BlackCard -> other.color == color
            is ColorCard -> other.color == color
            else -> false
        }
    }
}

class FunctionCard(
    override val color: CardColor,
    val function: CardFunction,
) : ColorCard(function.format) {
    override val name = "${color.name} ${function.text}"

    override fun canPutAbove(other: Card): Boolean {
        return when (other) {
            is FunctionCard -> other.color == color || other.function == function
            is BlackCard -> other.color == color
            is ColorCard -> other.color == color
            else -> false
        }
    }
}

open class BlackCard(
    val function: BlackCardFunction,
): Card {
    var color: CardColor? = null

    override fun canPutAbove(other: Card) = true
    override fun format(): String {
        return "black-${function.format}"
    }

    override val name = "Black ${function.text}"
}

abstract class ColorCard(
    val id: String
): Card {
    abstract val color: CardColor

    override fun format(): String {
        return "${color.format}-$id"
    }
}

interface Card {
    val name: String
    fun getImageUrl(): String? {
        return cardImages[format()]
    }

    fun format(): String

    fun canPutAbove(other: Card): Boolean
}

enum class BlackCardFunction(val text: String, val format: String) {
    Plus4("Plus 4", "plus-4"),
    ChangeColor("Change Color", "change-color")
}

enum class CardFunction(val text: String, val format: String) {
    Plus2("Plus 2", "plus-2"),
    Skip("Skip", "skip"),
    Reverse("Reverse", "reverse")
}

enum class CardColor(val format: String) {
    Red("red"), Green("green"), Blue("blue"), Yellow("yellow")
}