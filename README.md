# DUI - Discord UI
High-performance Kotlin Message Component Based UI Framework
<br>
Render Interactive Message and Manage States and Listeners

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