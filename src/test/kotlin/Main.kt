import command.SuperCommandModule
import command.builder.command
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.context.State
import net.sonmoosans.dui.utils.open
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.sonmoosans.dui.DUI
import net.sonmoosans.dui.component
import net.sonmoosans.dui.components.*
import net.sonmoosans.dui.hooks.sync
import net.sonmoosans.dui.hooks.useModal
import net.sonmoosans.dui.hooks.useState
import net.sonmoosans.dui.hooks.useSync
import net.sonmoosans.dui.utils.field
import net.sonmoosans.dui.utils.get
import net.sonmoosans.dui.utils.value

val example = component {
    val page = useState("page", 0)

    tabLayout(page) {
        tab("Todos") {
            text("Hello World")
            todo()
        }

        tab("Settings") {
            row {
                button("Close") {
                    event.delete()
                }
            }
        }
    }
}

val todo = component<Unit> {
    val todos = useState("todos", arrayListOf<String>())
    val addModal = useModal {
        title = "Add Todo"

        row {
            input("todo", "Todo Content")
        }

        submit {
            todos.value += event["todo"]

            event.edit()
        }
    }

    embed(title = "Todos") {
        for (todo in todos.value) {
            field(name = todo)
        }
    }

    row {
        button(label = "Add") {
            addModal.open(event)
        }
    }
}

fun main() {
    val jda = JDABuilder.createDefault(System.getenv("TOKEN"))
        .build()
        .awaitReady()

    DUI.install(jda)

    SuperCommandModule(
        TestCommand()
    ).init(jda)
}

fun TestCommand() = command("test", "Testing Command") {

    execute {
        val ui = example.create(event.user.idLong, Unit) {
            sync(event.hook)
        }

        event.reply(ui).queue()
    }
}