package io.krasch.openread.image

import android.graphics.Bitmap
import android.graphics.Matrix
import io.krasch.openread.geometry.types.Angle
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Point
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
