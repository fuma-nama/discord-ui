package net.sonmoosans.dui.bjda

import bjda.plugins.IModule
import net.dv8tion.jda.api.JDA
import net.sonmoosans.dui.DUI

class DUIModule : IModule {
    override fun init(jda: JDA) {
        DUI.install(jda)
    }
}