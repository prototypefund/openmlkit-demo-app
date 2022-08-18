package io.krasch.openread.image

import android.graphics.Bitmap
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import kotlin.math.ceil

fun ensureRectFitsIntoImage(image: Bitmap, rect: AngledRectangle): Pair<Bitmap, AngledRectangle> {
    val padLeftFloat = if (rect.left >= 0) 0.0 else -rect.left
    val padTopFloat = if (rect.top >= 0) 0.0 else -rect.top
    val padRightFloat = if (rect.right <= image.width) 0.0 else rect.right - image.width
    val padBottomFloat = if (rect.bottom <= image.height) 0.0 else rect.bottom - image.height

    // ceil because better to pad too much than too little
    val padLeft = ceil(padLeftFloat).toInt()
    val padTop = ceil(padTopFloat).toInt()
    val padRight = ceil(padRightFloat).toInt()
    val padBottom = ceil(padBottomFloat).toInt()

    // nothing to be done
    if ((padLeft == 0) && (padTop == 0) && (padRight == 0) && (padBottom == 0))
        return Pair(image, rect)

    val adjustedImage = pad(image, padLeft, padTop, padRight, padBottom)

    val adjustedRect = AngledRectangle(
        rect.bottomLeft + Point(padLeft, padTop),
        rect.width,
        rect.height,
        rect.angleBottom
    )

    return Pair(adjustedImage, adjustedRect)
}
