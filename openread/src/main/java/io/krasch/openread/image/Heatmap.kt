package io.krasch.openread.image

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.floor

// jet colourmap from cv2
val colourMap = listOf(
    Color.rgb(0, 0, 127),
    Color.rgb(0, 0, 241),
    Color.rgb(0, 76, 255),
    Color.rgb(0, 176, 255),
    Color.rgb(41, 255, 205),
    Color.rgb(124, 255, 121),
    Color.rgb(205, 255, 41),
    Color.rgb(255, 196, 0),
    Color.rgb(255, 103, 0),
    Color.rgb(241, 7, 0),
)

fun makeColourHeatmap(scoresFlat: List<Float>, width: Int, height: Int): Bitmap {
    require(width * height == scoresFlat.size)

    val pixels = scoresFlat.map {
        val colourId = floor(it * colourMap.size).toInt()
        colourMap[colourId]
    }

    val heatmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    heatmapImage.setPixels(pixels.toIntArray(), 0, width, 0, 0, width, height)

    return heatmapImage
}
