package utils

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

fun MessageEmbed.reply(event: IReplyCallback) = event.replyEmbeds(this)

inline fun MessageEmbed.reply(event: IReplyCallback, handler: ReplyCallbackAction.() -> Unit) = event.replyEmbeds(this).apply(handler)

