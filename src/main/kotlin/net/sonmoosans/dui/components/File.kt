package net.sonmoosans.dui.components

import net.sonmoosans.dui.context.Container
import net.sonmoosans.dui.context.RenderContextCreate
import net.sonmoosans.dui.context.RenderContextEdit
import net.dv8tion.jda.api.utils.FileUpload
import net.sonmoosans.dui.context.RenderContext
import net.sonmoosans.dui.utils.lambdaList
import java.io.File
import java.io.InputStream

/**
 * Add Attachment files
 */
fun<P: Any> RenderContextCreate<P>.addFiles(init: Container<FileUpload>.() -> Unit) {
    builder.addFiles(
        lambdaList(init)
    )
}

/**
 * Add Attachment files
 */
fun<P: Any> RenderContextEdit<P>.addFiles(init: Container<FileUpload>.() -> Unit) {

    builder.setAttachments(builder.attachments + lambdaList(init))
}

/**
 * Set Attachment files
 */
fun<P: Any> RenderContext<P, *>.files(init: Container<FileUpload>.() -> Unit) {

    builder.setFiles(
        lambdaList(init)
    )
}

fun Container<in FileUpload>.file(name: String, file: File) {
    add(FileUpload.fromData(file, name))
}

fun Container<in FileUpload>.file(name: String, resource: InputStream) {
    add(FileUpload.fromData(resource, name))
}