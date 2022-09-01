package net.sonmoosans.dui.utils

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.DiscordLocale.*
import net.sonmoosans.dui.context.DataContext

interface LocaleProps {
    val locale: DiscordLocale
}

operator fun DiscordLocale.invoke(
    default: String,
    vararg locales: LocalePair,
): String {
    for (locale in locales) {
        if (locale.match(this)) {
            return locale.text
        }
    }

    return default
}

fun DataContext<*, out LocaleProps>.locale(
    default: String,
    vararg locales: LocalePair,
): String {

    return locale(* locales)?: default
}

fun DataContext<*, out LocaleProps>.locale(
    vararg locales: LocalePair,
): String? {
    for (locale in locales) {
        if (locale.match(props.locale)) {
            return locale.text
        }
    }

    return null
}

/**
 * Localize given string
 */
fun DataContext<*, out LocaleProps>.locale(locales: Map<DiscordLocale, String>): String {
    return locales[props.locale]!!
}

/**
 * Localize given string
 */
fun DataContext<*, out LocaleProps>.locale(
    default: String,
    locales: Map<DiscordLocale, String>
): String {

    return locales[props.locale]
        ?: return default
}

abstract class LocalePair(val text: String) {
    abstract fun match(locale: DiscordLocale): Boolean
}

val DataContext<*, out LocaleProps>.locale get() = LocaleBuilder(props.locale)

class LocaleBuilder(val target: DiscordLocale) {
    var result: String? = null
        set(s) {
            if (field == null) field = s
        }

    operator fun plus(pair: LocalePair): LocaleBuilder {
        if (pair.match(target)) {
            result = pair.text
        }

        return this
    }

    operator fun plus(default: String): String {

        return result?: default
    }
}

fun english(text: String): LocalePair {
    return object : LocalePair(text) {
        override fun match(locale: DiscordLocale) =
            locale == ENGLISH_UK || locale == ENGLISH_US
    }
}

fun chinese(text: String): LocalePair {
    return object : LocalePair(text) {
        override fun match(locale: DiscordLocale) =
            locale == CHINESE_CHINA || locale == CHINESE_TAIWAN
    }
}

operator fun DiscordLocale.invoke(text: String): LocalePair {
    return object : LocalePair(text) {
        override fun match(locale: DiscordLocale) =
            locale == this@invoke
    }
}