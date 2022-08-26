package utils

import context.Container
import context.DslBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.*
import java.awt.Color
import java.time.OffsetDateTime

fun Embed(
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
    fields: (Container<Field>.() -> Unit)? = null,
): MessageEmbed {
    return MessageEmbed(
        url, title, description, type, timestamp, color.rgb,
        ThumbnailBuilder().build(thumbnail),
        ProviderBuilder().build(provider),
        AuthorBuilder().build(author),
        VideoBuilder().build(videoInfo),
        FooterBuilder().build(footer),
        ImageBuilder().build(image),
        if (fields != null) lambdaList(fields) else null
    )
}

fun Container<in Field>.field(name: String = "\u200e", value: String = "\u200e", inline: Boolean = true) {

    add(Field(name, value, inline))
}

class ImageBuilder: Builder<ImageInfo> {
    var url: String? = null
    var proxyUrl: String? = null
    var width = 0
    var height = 0

    override fun build() = ImageInfo(url, proxyUrl, width, height)
}

class FooterBuilder: Builder<Footer> {
    var text: String? = null
    var iconUrl: String? = null
    var proxyIconUrl: String? = null

    override fun build() = Footer(text, iconUrl, proxyIconUrl)
}

class VideoBuilder: Builder<VideoInfo> {
    var url: String? = null
    var width = 0
    var height = 0

    override fun build() = VideoInfo(url, width, height)
}

class ThumbnailBuilder: Builder<MessageEmbed.Thumbnail> {
    var url: String? = null
    var proxyUrl: String? = null
    var width = 0
    var height = 0

    override fun build() = MessageEmbed.Thumbnail(url, proxyUrl, width, height)
}

class ProviderBuilder: Builder<MessageEmbed.Provider> {
    var name: String? = null
    var url: String? = null

    override fun build() = MessageEmbed.Provider(name, url)
}

class AuthorBuilder: Builder<AuthorInfo> {
    lateinit var name: String
    var url: String? = null
    var iconUrl: String? = null
    var proxyIconUrl: String? = null

    override fun build() = AuthorInfo(name, url, iconUrl, proxyIconUrl)
}

fun<O, B: Builder<O>> B.build(apply: (B.() -> Unit)?): O? {
    apply?: return null

    apply(this)
    return build()
}

@DslBuilder
interface Builder<E> {
    fun build(): E
}