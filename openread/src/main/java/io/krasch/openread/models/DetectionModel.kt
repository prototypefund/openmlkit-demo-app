package io.krasch.openread.models

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import io.krasch.openread.geometry.algorithms.calculateConvexHull
import io.krasch.openread.geometry.algorithms.calculateMinAreaRectangle
import io.krasch.openread.geometry.algorithms.findConnectedComponents
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Array2D
import io.krasch.openread.geometry.types.Point
import io.krasch.openread.geometry.types.expandRect
import io.krasch.openread.image.resize2
import io.krasch.openread.tflite.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.MappedByteBuffer
import kotlin.math.floor
import kotlin.math.max

const val THRESHOLD_TEXT_FIRST_PASS = 0.2
const val THRESHOLD_TEXT_SECOND_PASS = 0.8
const val THRESHOLD_LINK = 0.2

data class DetectionResult(
    val heatmap: Bitmap,
    val boxes: List<AngledRectangle>
)

class DetectionModel(val model: ImageModel) {

    suspend fun run(bitmap: Bitmap): DetectionResult {
        // preprocessing
        val (resizeRatio, resizedBitmap) = resize2(bitmap, model.imageWidth, model.imageHeight)

        // model prediction
        Log.v("bla", "detection model run start")
        val (charHeatmap, linkHeatmap) = model.predict(resizedBitmap)
        Log.v("bla", "detection model run done")

        // val heatmapImage = makeColourHeatmap(charHeatmap, linkHeatmap, 1 / resizeRatio)
        // Log.v("bla", "heatmap done")

        // find connected components and calculate boxes around them
        val boxes = postprocess(charHeatmap, linkHeatmap, 1 / resizeRatio)

        return DetectionResult(bitmap, boxes.toList())
    }

    private fun postprocess(
        charHeatmap: Array<Float>,
        linkHeatmap: Array<Float>,
        resizeRatio: Double
    ) = sequence {

        // boolean array where True = this pixel is likely part of a piece of text
        val isText = charHeatmap.zip(linkHeatmap).map { (charScore, linkScore) ->
            (charScore >= THRESHOLD_TEXT_FIRST_PASS) || (linkScore >= THRESHOLD_LINK)
        }

        // to find connected components we need a 2D representation of the array
        val isText2D = Array2D(isText.toTypedArray(), model.imageHeight, model.imageWidth)

        // also need the char heatmap as a 2D array to do the second thresholding step
        val charHeatmap2D = Array2D(charHeatmap, model.imageHeight, model.imageWidth)

        // since components is a sequence, everything from here on is lazily executed
        val components = findConnectedComponents(isText2D)

        for (component in components) {
            // too small to be relevant
            if (component.size <= 10)
                continue

            // at least one pixel in the component should be above the second text threshold
            val maxScore = component.map { (row, col) -> charHeatmap2D[row, col] }.maxOf { it }
            if (maxScore < THRESHOLD_TEXT_SECOND_PASS)
                continue

            // let's go from array coordinates to points in our actual image coordinate system
            // beware: in array we use (row, col), in XY (x, y); so the reverse order!
            val points = component.map { (row, col) -> Point(col, row) }

            // since the input image was resized, the points need to be rescaled accordingly
            val scaledPoints = points.map { (x, y) ->
                Point(x * resizeRatio, y * resizeRatio)
            }

            // convex hull that covers all of the points in the component
            val hull = calculateConvexHull(scaledPoints)

            // rectangle around the convex hull
            val rect = calculateMinAreaRectangle(hull)

            if (rect != null) { // todo why can this be null?
                val expandedRect = expandRect(rect, 0.5)
                if (expandedRect.width > expandedRect.height) {
                    yield(expandedRect)
                }
            }
        }
    }

    private fun makeColourHeatmap(
        charHeatmap: Array<Float>,
        linkHeatmap: Array<Float>,
        resizeRatio: Double
    ): Bitmap {

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

        val combined = charHeatmap.zip(linkHeatmap).map { (char, link) ->
            max(char, link)
        }

        val pixels = combined.map {
            val colourId = floor(it * colourMap.size).toInt()
            colourMap[colourId]
        }

        val heatmapImage = Bitmap.createBitmap(model.imageWidth, model.imageHeight, Bitmap.Config.ARGB_8888)
        heatmapImage.setPixels(
            pixels.toIntArray(),
            0, model.imageWidth, 0, 0, model.imageWidth, model.imageHeight
        )

        /* todo
        return heatmapImage.scale(
            (model.imageWidth * resizeRatio).toInt(),
            (model.imageHeight * resizeRatio).toInt()
        )*/
        return heatmapImage
    }

    companion object {
        suspend fun initialize(modelFile: MappedByteBuffer): DetectionModel {

            val model = withContext(Dispatchers.IO) {
                ImageModel(modelFile, hasGPUSupport = true)
            }

            return DetectionModel(model)
        }
    }
}
