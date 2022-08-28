import bjda.bjda
import bjda.plugins.supercommand.builder.command
import bjda.plugins.supercommand.supercommand
import bjda.wrapper.Mode
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.sonmoosans.dui.bjda.DUIModule
import net.sonmoosans.dui.component
import net.sonmoosans.dui.components.*
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.hooks.sync
import net.sonmoosans.dui.hooks.useModal
import net.sonmoosans.dui.hooks.useState
import net.sonmoosans.dui.utils.*

data class Props(override val locale: DiscordLocale): LocaleProps

val example = component<Props> {
    println("Hooks: ${data.hooks.size}")
    println("States: ${data.states.size}")
    println("Listeners: ${component.listeners.size}")

    tabLayout(scope = "Tab1") {
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

    tabLayout(scope = "Tab2") {
        tab(
            locale("Todos", chinese("待辦事項"))
        ) {
            text(locale + chinese("Chinese") + english("English") + "Default")
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

fun RenderContext<*, *>.todo() {
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

suspend fun main() {
    bjda(Mode.Default) {
        config {
            setToken(System.getenv("TOKEN"))
        }

        supercommand(
            TestCommand()
        )

        + DUIModule()
    }
}

fun TestCommand() = command("test", "Testing Command") {

    execute {
        val ui = example.create(event.user.idLong, Props(locale = event.userLocale)) {
            sync(event.hook)
        }

        event.reply(ui).queue()
    }
}