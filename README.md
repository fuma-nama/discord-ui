<div align="center">
    <img src="./document/Icon.jpg" alt="banner" width="600px"/>
</div>

# DUI - Discord UI ![GitHub](https://img.shields.io/github/license/SonMooSans/discord-ui?style=for-the-badge) ![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.sonmoosans/dui?server=https%3A%2F%2Fs01.oss.sonatype.org) ![GitHub Repo stars](https://img.shields.io/github/stars/SonMooSans/discord-ui?style=social)
High-performance Discord Message Component Based Kotlin UI Framework
<br>
Render Interactive Message and Manage States and Listeners

## Installation
```xml
<dependency>
    <groupId>io.github.sonmoosans</groupId>
    <artifactId>dui</artifactId>
    <version>1.1.2</version>
</dependency>
```

## Features
DUI provides high code quality, high performance, memory safe UI System

### React.js Style Components
```kotlin
val counter = component<Unit> {
    val count = useState("count", 0)

    embed(title = "Counter", description = count.asString())

    row {
        button("Increase") {
            count.value++
            event.edit()
        }
    }
}
```

### Useful Hooks
Built-in Hook IDs can also be anonymous, including `useState`
```kotlin
val theme = useContext(ThemeContext)
val state = useState("id", "initial value")
val sync = useSync()
val memo = useMemo(dependencies) { processString(state.value) }
val confirmModal = useModal {
    title = "Do you sure?"

    row {
        input(id = "confirm", label = "Type 'I am gay' to confirm")
    }

    submit {
        //do something
    }
}

useEffect(dependencies) {
    println("Updated!")
}
```

### Built-in Components
DUI also provides some built-in Components

```kotlin
rowLayout { //Split into multi Action Rows if overflow
    button(label = "Test") {
        event.ignore()
    }

    menu {
        option("Label", "Value")

        submit {
            event.ignore()
        }
    }
}
pager { //a simple Pager implementation
    page {
        embed(title = "Page 1")
    }
}
tabLayout { //Adds a SelectMenu to switch between Tabs
    tab("User") {
        text("Your Profile")
        proflie()
    }

    tab("Settings") {
        embed(title = "Settings Tab")
    }
}
```

### Memory Safe Dynamic Listeners
Components and Listeners only needs to be created once, and can be used for multi Entries/Messages
<br>
Every entry contains a unique Data used for Rendering, All Data will be stored in a Map

Dynamic ID Structure: `[Component ID]-[Data ID]-[Listener ID]`
<br>
You **must** destroy unused Data manually

```kotlin
row {
    button("Do Something") { //ID: 4343243243-3-432423432
        println("Component Interaction Event")
    }

    button("Remove", id = "onRemove") { //ID: 4343243243-3-onRemove
        println("Component Interaction Event")
    }
}
```

## Getting Started
Create a Component
```kotlin
val example = component {
    val count = useState("count", 0)

    text(count.asString())

    row {
        button("Increase") {
            count.value++
            event.edit()
        }
    }
}
```
In above example, we create a `count` state
<br>
When "Increase" Button is clicked, Increase count state and Reply to the event

Then, Register a Slash command (We use [BJDA](https://github.com/SonMooSans/B-JDA) for this)
<br>
See their tutorial to learn how to use BJDA
```kotlin
fun TestCommand() = command("test", "Testing Command") {

    execute {
        val ui = example.create(event.user.idLong, Unit) {
            sync(event.hook)
        }

        event.reply(ui).queue()
    }
}
```

## Known issues
* Dynamic Listeners unmounted after restarting bot

## Support My Job
Give this repo a star!