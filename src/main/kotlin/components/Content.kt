package components

import context.RenderContext
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import utils.Embed
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
    thumbnail: MessageEmbed.Thumbnail? = null,
    provider: MessageEmbed.Provider? = null,
    author: MessageEmbed.AuthorInfo? = null,
    videoInfo: MessageEmbed.VideoInfo? = null,
    footer: MessageEmbed.Footer? = null,
    image: MessageEmbed.ImageInfo? = null,
    fields: List<MessageEmbed.Field>? = null,
) {
    builder.setEmbeds(
        builder.embeds + Embed(url, title, description, type, timestamp, color, thumbnail, provider, author, videoInfo, footer, image, fields)
    )
}
