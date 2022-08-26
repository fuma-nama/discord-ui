import command.SuperCommandModule
import command.builder.command
import components.*
import hooks.useEffect
import hooks.useMemo
import listeners.ComponentListener
import utils.open
import hooks.useModal
import hooks.useState
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import utils.field
import utils.get
import utils.value

val example = component {
    val count = useState("name", 0)

    val deleteModal = useModal {
        title = "Do You sure You want to Delete this Message?"

        row {
            input("name", "Type ${count.asString()} to delete")
        }

        submit {
            if (event["name"] == count.asString()) {
                event.delete()
            } else {
                event.ignore()
            }
        }
    }

    useEffect {
        println("Init")
    }

    useEffect(count.value) {
        println("Count updated")
    }

    val memo = useMemo(count.value) {
        println("processing")

        count.value + 1
    }

    embed(
        title = props,
        description = count.asString(),
        fields = {
            field("Memo", memo.toString())
            field("Test", "Hello")
        }
    )

    rowLayout {
        button("Increase") {
            count.value += 1

            event.edit()
        }

        button("Decrease") {
            count.value -= 1

            event.edit()
        }

        button("Close", style = ButtonStyle.DANGER) {
            deleteModal.open(event)
        }

        menu(placeholder = "Select a Value", selected = count.value) {
            option("Small", "0")
            option("Big", "10")

            submit {
                count *= event.value().toInt()

                event.delete()
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