package net.sonmoosans.dui

import net.dv8tion.jda.api.JDA
import net.sonmoosans.dui.listeners.ComponentListener

object DUI {
    fun install(jda: JDA) {
        jda.addEventListener(ComponentListener)
    }
}