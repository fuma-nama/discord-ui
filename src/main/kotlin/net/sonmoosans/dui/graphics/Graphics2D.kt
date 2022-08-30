package net.sonmoosans.dui.graphics

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun Graphics2D.drawStringCenter(string: String, w: Int, h: Int) {
    drawString(string, (w - fontMetrics.stringWidth(string)) / 2, (h + (fontMetrics.height/2)) / 2)
}

inline fun Graphics2D.paint(color: Color, then: Graphics2D.() -> Unit) {
    val prev = paint
    paint = color
    then(this)

    paint = prev
}

fun BufferedImage.toInputStream(): ByteArrayInputStream {
    val output = ByteArrayOutputStream()

    ImageIO.write(this, "png", output)
    return ByteArrayInputStream(output.toByteArray(), 0, output.size())
}