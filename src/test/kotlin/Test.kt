import utils.generateCard

fun main() {
    for (i in (0..10)) {
        val card = generateCard()
        println(card.name)
    }
}