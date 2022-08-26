import command.SuperCommandModule
import command.builder.command
import components.*
import hooks.useEffect
import hooks.useMemo
import listeners.ComponentListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import utils.value

val example = component {
    val count = useState("name", 0)

    useEffect("name") {
        println("Init")
    }

    useEffect("count", count.value) {
        println("Count updated")
    }

    val memo = useMemo(count.value) {
        println("processing")

        count.value + 1
    }

    text(props)
    text(count.asString(), TextType.AppendLine)

    rowLayout {
        button("Do Nothing") {
            edit()
        }

        button("Increase") {
            count.value += 1

            edit()
        }

        button("Decrease") {
            count.value -= 1

            edit()
        }

        button("Close", style = ButtonStyle.DANGER) {
            delete()
        }

        menu(placeholder = "Select a Value", selected = count.value) {
            option("Small", "0")
            option("Big", "10")

            submit {
                count *= event.value().toInt()

                edit()
            }
        }
    }
}

fun main() {
    val jda = JDABuilder.createDefault("OTA3OTU1NzgxOTcyOTE4Mjgz.Gr4wp2.kgCl7znvdc3jRlP8Je46QB2M4_p9vu2VB1gbcE")
        .addEventListeners(ComponentListener())
        .build()
        .awaitReady()

    SuperCommandModule(
        TestCommand()
    ).init(jda)
}

fun TestCommand() = command("test", "Testing Command") {

    execute {

        event.reply(example.create("Hello World")).queue()
    }
}