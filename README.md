<div align="center">
    <img src="./document/Icon.jpg" alt="banner" width="600px"/>
</div>

# DUI - Discord UI ![GitHub](https://img.shields.io/github/license/SonMooSans/discord-ui?style=for-the-badge) ![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.sonmoosans/dui?server=https%3A%2F%2Fs01.oss.sonatype.org) ![GitHub Repo stars](https://img.shields.io/github/stars/SonMooSans/discord-ui?style=social)
High-performance Discord Message Component Based Kotlin UI Framework
<br>
Render Reactive Message and Manage States and Listeners

## Installation
```xml
<dependency>
    <groupId>io.github.sonmoosans</groupId>
    <artifactId>dui</artifactId>
    <version>1.4.0</version>
</dependency>
```

## Features
DUI provides high code quality, high performance, memory safe UI System
* Manage Message Component Listeners
* Rendering message like a reactive UI
* Functional Programming style usage
* Highly flexible: Rendering with everything such as `Graphics2d`
* Memory-Safe: We use dynamic listener to reduce memory usage

## Functional Programming Style
Everything is Clean and Beautiful.

> ### Example
> Create a message containing an Embed that displays a number
> <br>
> With a button that increases the number by clicking it
```kotlin
val counter = component<Unit> {
    val count by useState("count", 0)

    embed(title = "Counter", description = count.toString())

    row {
        button("Increase") {
            count++
            event.edit()
        }
    }
}
```

## Useful Hooks
DUI provided some built-in hooks for rendering messages

All Hooks contain a unique ID field so it can be called in any orders
<br>
ID of Built-in Hooks can be anonymous, which is generated from lambda

```kotlin
val theme = useContext(ThemeContext)
val state by useState("id", "initial value")
val (count, setCount) = useState { "initial value" }
val sync = useSync()
val memo = useMemo(dependencies) { processString(state) }
val confirmModal = useModal {
    title = "Do you sure?"

    row {
        input(id = "confirm", label = "Type 'I am gay' to confirm")
    }

    submit {
        //do something
    }
}

useChange(dependencies) {
    println("Updated!")
}
useEffect(dependencies) {
    println("Updated!")
}
useExport(data = "Export Something")
```

## Built-in Components
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

## Memory Safe
Component only needs a `Data` instance for rendering.

For `IDComponent`, All those Data will be stored in a Map
<br>
You can implement your own management system above it
<br>
Remember that You might need to **destroy** unused Data manually

### Dynamic Components
```kotlin
val Test = dynamicComponent(YourGenerator) {
    embed(title = props.value)
}
```
It reads props from event info using a `Generator` and invoke dynamic listeners with parsed data
<br>
Therefore, it doesn't require to store any data

However, you cannot store too many things to a listener id since length of listener Ids has a limit

## Component Listeners
### Data Based Listeners
`IDComponent`, `OnceComponent` use it as the default listener type

```kotlin
val result = something()
button("Do something", dynamic = false) {
    println(result)
    event.edit()
}
```
Data Based Listeners are stored in each `Data` object
<br>
Therefore, It will use some memory when there are a lot of data objects and listeners

### Dynamic Listener
`SingleDataComponent`, `DynamicComponent` use it as the default listener type
```kotlin
val ref = useRef { something() }
button("Do Something", dynamic = true) {
    println(ref.current)
    event.edit()
}
```
You can use **Dynamic Listener** instead to reduce memory usage

Since they are bundled with Component itself
<br>
Dynamic Listeners only needs to be created once, and can be used for unlimited times.

Since data is not synchronized, You should not access any data outside the Listener
<br>
**You must wrap those variables inside a `useRef` hook to access them**

### Change Default type of listener
```kotlin
//set default value of 'dynamic'
dynamic = value
button("...") {
    //do something
}

//set default value of 'dynamic' only in scope
dynamic(value) {
    button("...") {
        //do something
    }
}
```
### Note
Data based Listeners can override dynamic listeners by using the same ID
### Listener ID Structure
Listener ID Structure: `[Component ID]-[Data ID]-[Listener ID]`

```kotlin
row {
    button("Do Something") { //ID: 4343243243-3-432423432
        println("Component Interaction Event")
    }

    button("Remove", id = "onRemove") { //ID: 4343243243-3-onRemove
        println("Component Interaction Event")
    }
    
    menu(placeholder = "Select Item") {
        option("...", "...")
        
        submit("onSelect") { //ID: 4343243243-3-onSelect
        }
    }
}
```
To use external Listener ID, don't pass the event handler
<br>
Therefore, you can create your own Event handler
<br>
```kotlin
row {
    //For Select Menu, just pass the ID to root function instead of 'submit' function
    menu(id = "onRemove", placeholder = "Select Something") {
        option("...", "...")
    }
    button("Remove", id = "onRemove") //ID: onRemove
}
```

### Highly Flexible
<img src="./document/todo-example.jpg" alt="banner" width="200px"/>

Not only embed or text, DUI supports render everything. Including rendering UI with Graphics2D
<br>
DUI also has a small Utility for Rendering with Graphics2D

```kotlin
//You may wrap this in useMemo Hook
val image = BufferedImage(500, 600, BufferedImage.TYPE_INT_RGB)

with (image.createGraphics()) {
    val (w, h) = 450 to 100

    font = font.deriveFont(25f)
    translate((500 - w) / 2, 50)

    for (i in 0..3) {
        paint(Color.DARK_GRAY) {
            fillRoundRect(0, 0, w, h, 20, 20)
        }

        translate(0, h + 10)
    }
}

files {
    file("ui.png", image.toInputStream())
}
```

## Getting Started
Create a Component
```kotlin
val example = component {
    val count by useState("count", 0)
    
    text(count.toString())

    row {
        button("Increase") {
            count++
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
            //sync(event.hook)
            //use with useSync hook to sync multi messages
        }

        event.reply(ui).queue()
    }
}
```

## Known issues
* Dynamic Listeners unmounted after restarting bot

## Support My Job
Give this repo a star!
