package net.sonmoosans.dui.components

import net.sonmoosans.dui.context.Container
import net.sonmoosans.dui.context.RenderContext
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.sonmoosans.dui.utils.*
import java.awt.Color
import java.time.OffsetDateTime

fun RenderContext<*, *>.text(content: String, type: TextType = TextType.Set) {
    with (builder) {
        when (type) {
            TextType.Set -> setContent(content)
            TextType.Append -> setContent(this.content + content)
            TextType.AppendLine -> setContent("${this.content}\n$content")
        }
    }
}

enum class TextType {
    Set, Append, AppendLine
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
