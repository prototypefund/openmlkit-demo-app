package io.krasch.openread.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import io.krasch.openread.geometry.types.Angle
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

fun resize(
    image: Bitmap,
    targetWidth: Int,
    targetHeight: Int,
    keepRatio: Boolean = false
): Bitmap {

    if (!keepRatio)
        return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, true)

    val scaled = scale(image, targetWidth, targetHeight)
    // Log.v("bla", "${image.width} ${image.height} -> ${scaled.width} ${scaled.height}")
    return pad(scaled, targetWidth, targetHeight)
}

fun scale(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {

    val ratioWidth = maxWidth.toFloat() / image.width
    val ratioHeight = maxHeight.toFloat() / image.height

    var newWidth = floor(image.width * ratioWidth).toInt()
    var newHeight = floor(image.height * ratioWidth).toInt()

    if ((newWidth > maxWidth) or (newHeight > maxHeight)) {
        newWidth = floor(image.width * ratioHeight).toInt()
        newHeight = floor(image.height * ratioHeight).toInt()
    }

    return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
}

fun pad(image: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
    if ((image.width > targetWidth) or (image.height > targetHeight))
        throw IllegalArgumentException("Can not pad image that is already too large")

    // nothing to be done
    if ((image.width == targetWidth) and (image.height == targetHeight))
        return image

    val padded = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(padded)

    // background color
    val paint = Paint()
    paint.color = Color.WHITE
    paint.style = Paint.Style.FILL
    canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

    // draw image in top left corner -> padding at right and bottom
    canvas.drawBitmap(image, 0f, 0f, null)

    return padded
}

// todo integrate with other pad function
fun pad(image: Bitmap, left: Double, top: Double, right: Double, bottom: Double): Bitmap {
    val paddedImage = Bitmap.createBitmap(
        (image.width + left + right).toInt(),
        (image.height + top + bottom).toInt(),
        Bitmap.Config.ARGB_8888
    )
    paddedImage.eraseColor(Color.WHITE)

    val canvas = Canvas(paddedImage)
    canvas.drawBitmap(image, left.toFloat(), top.toFloat(), null)

    return paddedImage
}

fun ensureRectFitsIntoImage(image: Bitmap, rect: AngledRectangle): Pair<Bitmap, AngledRectangle> {
    val padLeft = if (rect.left >= 0) 0.0 else -rect.left
    val padTop = if (rect.top >= 0) 0.0 else -rect.top
    val padRight = if (rect.right <= image.width) 0.0 else rect.right - image.width
    val padBottom = if (rect.bottom <= image.height) 0.0 else rect.bottom - image.height

    // nothing to be done
    if ((padLeft == 0.0) && (padTop == 0.0) && (padRight == 0.0) && (padBottom == 0.0))
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

fun expandRect(rect: AngledRectangle, ratio: Double): AngledRectangle {
    val diffWidth = rect.width * ratio
    val diffHeight = rect.height * ratio

    val shiftBottomLeft = Point(-diffWidth / 2.0, + diffHeight / 2.0)
    val newBottomLeft = (rect.bottomLeft.rotate(-rect.angleBottom) + shiftBottomLeft).rotate(rect.angleBottom)

    return AngledRectangle(
        newBottomLeft,
        rect.width + diffWidth,
        rect.height + diffHeight,
        rect.angleBottom
    )
}

fun rotateAndCutout(image: Bitmap, rect: AngledRectangle): Bitmap {
    require(rect.width > rect.height) { "Rectangle must be wide" }

    val (adjustedImage, adjustedRect) = ensureRectFitsIntoImage(image, rect)

    /*val mutable = adjustedImage.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutable)
    drawBoundingBox(canvas, adjustedRect)
    return mutable*/

    // rough rotation and cutout
    val matrix = Matrix()
    matrix.setRotate(
        adjustedRect.angleBottom.degree.toFloat(),
        adjustedRect.bottomLeft.x.toFloat(),
        adjustedRect.bottomLeft.y.toFloat()
    )

    val rotatedImage = Bitmap.createBitmap(
        adjustedImage,
        adjustedRect.left.toInt(),
        adjustedRect.top.toInt(),
        adjustedRect.boxWidth.toInt(),
        adjustedRect.boxHeight.toInt(),
        matrix,
        true
    )

    val absRadian = abs(rect.angleBottom.radian)
    val rotatedRectangle = AngledRectangle(
        Point(
            sin(absRadian) * rect.height * cos(absRadian),
            sin(absRadian) * rect.width * cos(absRadian) + rect.height
        ),
        rect.width,
        rect.height,
        Angle.fromDegree(0)
    )

    // fine rotation
    val result = Bitmap.createBitmap(
        rotatedImage,
        rotatedRectangle.left.toInt(),
        rotatedRectangle.top.toInt(),
        rotatedRectangle.width.toInt(),
        rotatedRectangle.height.toInt()
    )

    return result
}
