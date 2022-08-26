import command.SuperCommandModule
import command.builder.command
import components.*
import hooks.*
import listeners.ComponentListener
import utils.open
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import utils.field
import utils.get
import utils.value

val example = component {
    val sync = useSync()
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

    embed(
        title = props,
        description = count.asString(),
        fields = {
            field("Test", "Hello")
        }
    )
    rowLayout {
        button("Increase") {
            count.value += 1

            event.edit()
            sync(event.hook)
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

                event.edit()
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
        val ui = example.create(event.user.id, "Hello World") {
            sync(event.hook)
        }

        event.reply(ui).queue()
    }
}