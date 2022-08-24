package io.krasch.openread.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color


private fun repeatColumn(image: Bitmap, sourceColumn: Int, targetColumns: IntRange) {
    if (targetColumns.isEmpty())
        return

    val pixels = IntArray(image.height)
    image.getPixels(pixels, 0, 1, sourceColumn, 0, 1, image.height)

    for (x in targetColumns) {
        image.setPixels(pixels, 0, 1, x, 0, 1, image.height)
    }
}

private fun repeatRow(image: Bitmap, sourceRow: Int, targetRows: IntRange) {
    if (targetRows.isEmpty())
        return

    val pixels = IntArray(image.width)
    image.getPixels(pixels, 0, image.width, 0, sourceRow, image.width, 1)

    for (y in targetRows) {
        image.setPixels(pixels, 0, image.width, 0, y, image.width, 1)
    }
}

fun pad(
    image: Bitmap,
    padLeft: Int = 0, padTop: Int = 0, padRight: Int = 0, padBottom: Int = 0,
    repeatEdge: Boolean = false
): Bitmap {

    // nothing to be done
    if ((padLeft == 0) && (padTop == 0) && (padRight == 0) && (padBottom == 0))
        return image

    val padded = Bitmap.createBitmap(
        image.width + padLeft + padRight,
        image.height + padTop + padBottom,
        Bitmap.Config.ARGB_8888
    )
    padded.eraseColor(Color.BLACK)

    val canvas = Canvas(padded)
    canvas.drawBitmap(image, padLeft.toFloat(), padTop.toFloat(), null)

    if (repeatEdge) {
        // fill padLeft with first column of original image
        repeatColumn(padded,padLeft+1,0 until padLeft)

        // fill padRight with last column of original image
        repeatColumn(padded,image.width - 1,image.width until image.width + padRight)

        // fill padTop with first row of original image
        repeatRow(padded,padTop+1,0 until padTop)

        // fill padBottom with last row of original image
        repeatRow(padded,image.height - 1,image.height until image.height + padBottom)
    }

    return padded
}

fun unpad(image: Bitmap, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0): Bitmap {
    val originalWidth = image.width - left - right
    val originalHeight = image.height - top - bottom

    return Bitmap.createBitmap(image, left, top, originalWidth, originalHeight)
}
