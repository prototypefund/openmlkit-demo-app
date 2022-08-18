package io.krasch.openread.image

import android.graphics.Bitmap
import kotlin.math.min

data class ResizeParameters(
    val ratio: Double,
    val padRight: Int,
    val padBottom: Int
)

data class ImageDimensions(
    val width: Int,
    val height: Int
)

fun getSizeWithoutPadding(original: ImageDimensions, ratio: Double): ImageDimensions {
    return ImageDimensions(
        width = (original.width * ratio).toInt(),
        height = (original.height * ratio).toInt()
    )
}

fun calculateResizeRatioParameters(
    original: ImageDimensions,
    target: ImageDimensions
): ResizeParameters {

    val ratioWidth = target.width.toDouble() / original.width.toDouble()
    val ratioHeight = target.height.toDouble() / original.height.toDouble()

    val ratio = min(ratioWidth, ratioHeight)

    val sizeWithoutPadding = getSizeWithoutPadding(original, ratio)

    val padRight = target.width - sizeWithoutPadding.width
    val padBottom = target.height - sizeWithoutPadding.height

    return ResizeParameters(ratio, padRight, padBottom)
}

fun resizeRatio(
    image: Bitmap,
    targetWidth: Int,
    targetHeight: Int
): Pair<Bitmap, ResizeParameters> {

    val sizeOriginal = ImageDimensions(image.width, image.height)
    val sizeTarget = ImageDimensions(targetWidth, targetHeight)

    val resizeParameters = calculateResizeRatioParameters(sizeOriginal, sizeTarget)

    val sizeWithoutPadding = getSizeWithoutPadding(sizeOriginal, resizeParameters.ratio)

    val resized = Bitmap.createScaledBitmap(
        image,
        sizeWithoutPadding.width,
        sizeWithoutPadding.height, true
    )

    val padded = pad(resized, right = resizeParameters.padRight, bottom = resizeParameters.padBottom)

    return Pair(padded, resizeParameters)
}
