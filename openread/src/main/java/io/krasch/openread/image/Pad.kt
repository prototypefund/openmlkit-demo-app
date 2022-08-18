package io.krasch.openread.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

fun pad(image: Bitmap, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): Bitmap {
    // nothing to be done
    if ((left == 0) && (top == 0) && (right == 0) && (bottom == 0))
        return image

    val paddedImage = Bitmap.createBitmap(
        image.width + left + right,
        image.height + top + bottom,
        Bitmap.Config.ARGB_8888
    )
    paddedImage.eraseColor(Color.WHITE)

    val canvas = Canvas(paddedImage)
    canvas.drawBitmap(image, left.toFloat(), top.toFloat(), null)

    return paddedImage
}
