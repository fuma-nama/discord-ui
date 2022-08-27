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

fun RenderContext<*, *>.counter(count: State<Int>) {

    embed(
        title = "Counter",
        description = count.asString(),
    )

    rowLayout {
        menu(placeholder = "Select a Value", selected = count.value) {
            option("Small", "0")
            option("Big", "10")

            submit {
                count *= event.value().toInt()

                event.edit()
            }
        }

        button("Increase") {
            count.value += 1

            event.edit()
        }

        button("Decrease") {
            count.value -= 1

            event.edit()
        }
    }
}

val example = component<Unit> {
    val sync = useSync()
    val count = useState("count", 0)
    val deleteModal = useModal {
        title = "Do You sure You want to Delete this Message?"

        row {
            input("name", "Type ${count.asString()} to delete")
        }

        submit {
            if (event["name"] == count.asString()) {
                sync.delete(event)
            } else {
                event.ignore()
            }
        }
    }

    counter(count)

    row {
        button("Sync") {
            sync.edit(event)
        }

        button("Close", style = ButtonStyle.DANGER) {
            deleteModal.open(event)
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
        val ui = todo.create(event.user.id, Unit) {
            sync(event.hook)
        }

        event.reply(ui).queue()
    }
}