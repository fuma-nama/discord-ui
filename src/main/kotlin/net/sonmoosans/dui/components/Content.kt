package net.sonmoosans.dui.components

import net.sonmoosans.dui.context.Container
import net.sonmoosans.dui.context.RenderContext
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.sonmoosans.bjda.utils.text
import net.sonmoosans.dui.utils.*
import java.awt.Color
import java.time.OffsetDateTime

/**
 * Append Message Content
 */
fun RenderContext<*, *>.text(content: String) {
    builder.text += content
}

/**
 * Append Message Content Line
 */
fun RenderContext<*, *>.textln(content: String) {
    builder.text += (content + "\n")
}

/**
 * Append Code Line to Message Content
 */
fun RenderContext<*, *>.code(content: String) {
    builder.text += "`$content`"
}

/**
 * Append Code Line to Message Content
 */
fun RenderContext<*, *>.codeln(content: String) {
    builder.text += "`$content`\n"
}


/**
 * Append Code Line to Message Content
 */
fun RenderContext<*, *>.codeBlock(content: String, language: String? = null) {
    builder.text += """
        ```${language.orEmpty()}
        $content
        ```
    """.trimIndent()
}

/**
 * Set MessageContent
 */
fun RenderContext<*, *>.content(content: String) {
    builder.setContent(content)
}

fun RenderContext<*, *>.embed(
    url: String? = null,
    title: String? = null,
    description: String? = null,
    type: EmbedType = EmbedType.RICH,
    timestamp: OffsetDateTime? = null,
    color: Color = Color.BLACK,
    thumbnail: (ThumbnailBuilder.() -> Unit)? = null,
    provider: (ProviderBuilder.() -> Unit)? = null,
    author: (AuthorBuilder.() -> Unit)? = null,
    videoInfo: (VideoBuilder.() -> Unit)? = null,
    footer: (FooterBuilder.() -> Unit)? = null,
    image: (ImageBuilder.() -> Unit)? = null,
    fields: (Container<MessageEmbed.Field>.() -> Unit)? = null,
) {
    builder.setEmbeds(
        builder.embeds + Embed(url, title, description, type, timestamp, color, thumbnail, provider, author, videoInfo, footer, image, fields)
    )
}
