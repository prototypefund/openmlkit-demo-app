package io.krasch.openread.image

import android.graphics.Bitmap
import kotlin.math.min

data class ResizeConfig(
    val ratio: Double,
    val padRight: Int,
    val padBottom: Int
)

data class ImageDimensions(
    val width: Int,
    val height: Int
)

fun calculateResizeConfig(
    original: ImageDimensions,
    target: ImageDimensions
): ResizeConfig {

    val ratioWidth = target.width.toDouble() / original.width.toDouble()
    val ratioHeight = target.height.toDouble() / original.height.toDouble()

    val ratio = min(ratioWidth, ratioHeight)

    val sizeWithoutPadding = ImageDimensions(
        width = (original.width * ratio).toInt(),
        height = (original.height * ratio).toInt()
    )

    val padRight = target.width - sizeWithoutPadding.width
    val padBottom = target.height - sizeWithoutPadding.height

    return ResizeConfig(ratio, padRight, padBottom)
}

fun resize(image: Bitmap, ratio: Double): Bitmap {
    val resizedWidth = (image.width * ratio).toInt()
    val resizedHeight = (image.height * ratio).toInt()

    return Bitmap.createScaledBitmap(image, resizedWidth, resizedHeight, true)
}

fun resizeWithPadding(
    image: Bitmap,
    targetWidth: Int,
    targetHeight: Int,
    repeatEdge: Boolean = false
): Pair<Bitmap, ResizeConfig> {

    val sizeOriginal = ImageDimensions(image.width, image.height)
    val sizeTarget = ImageDimensions(targetWidth, targetHeight)

    val resizeConfig = calculateResizeConfig(sizeOriginal, sizeTarget)

    val resized = resize(image, resizeConfig.ratio)

    val padded = pad(resized,
        padRight = resizeConfig.padRight,
        padBottom = resizeConfig.padBottom,
        repeatEdge = repeatEdge)

    return Pair(padded, resizeConfig)
}

fun undoResizeWithPadding(image: Bitmap, resizeConfig: ResizeConfig): Bitmap {
    val imageWithoutPadding = unpad(image, right = resizeConfig.padRight, top = resizeConfig.padBottom)
    val imageOriginalSize = resize(imageWithoutPadding, 1.0 / resizeConfig.ratio)
    return imageOriginalSize
}
