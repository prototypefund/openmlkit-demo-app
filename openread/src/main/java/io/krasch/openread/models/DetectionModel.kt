package io.krasch.openread.models

import android.graphics.Bitmap
import android.util.Log
import io.krasch.openread.geometry.algorithms.calculateConvexHull
import io.krasch.openread.geometry.algorithms.calculateMinAreaRectangle
import io.krasch.openread.geometry.algorithms.findConnectedComponents
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Array2D
import io.krasch.openread.geometry.types.Point
import io.krasch.openread.geometry.types.expandRect
import io.krasch.openread.image.ResizeConfig
import io.krasch.openread.image.makeColourHeatmap
import io.krasch.openread.image.resizeWithPadding
import io.krasch.openread.image.undoResizeWithPadding
import io.krasch.openread.tflite.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.MappedByteBuffer
import kotlin.math.max

const val THRESHOLD_TEXT_FIRST_PASS = 0.4
const val THRESHOLD_TEXT_SECOND_PASS = 0.7
const val THRESHOLD_LINK = 0.2

data class DetectionResult(
    val heatmap: Bitmap,
    val boxes: List<AngledRectangle>
)

class DetectionModel(val model: ImageModel) {

    suspend fun run(bitmap: Bitmap): DetectionResult {
        // preprocessing
        val (resizedBitmap, resizeParams) = resizeWithPadding(bitmap, model.inputWidth, model.inputHeight)

        // model prediction
        Log.v("bla", "detection model run start")
        val (charHeatmap, linkHeatmap) = model.predict(resizedBitmap)
        Log.v("bla", "detection model run done")

        // nice colour representation of the heatmaps, good for debugging
        val heatmapImage = prepareColourHeatmap(charHeatmap, linkHeatmap, resizeParams)

        // find connected components and calculate boxes around them
        val boxes = postprocess(charHeatmap, linkHeatmap, 1 / resizeParams.ratio)

        return DetectionResult(heatmapImage, boxes.toList())
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
        val isText2D = Array2D(isText.toTypedArray(), model.inputHeight, model.inputWidth)

        // also need the char heatmap as a 2D array to do the second thresholding step
        val charHeatmap2D = Array2D(charHeatmap, model.inputHeight, model.inputWidth)

        // since components is a sequence, everything from here on is lazily executed
        val components = findConnectedComponents(isText2D)

        for (component in components) {
            // too small to be relevant
            if (component.size <= 10)
                continue

            // these pixels clear the second, higher, threshold, i.e. very likely to be text
            val intensePixels = component.filter { (row, col) ->
                charHeatmap2D[row, col] >= THRESHOLD_TEXT_SECOND_PASS
            }

            // only keep components that have some percentage of such intense pixel
            if (intensePixels.size.toFloat() / component.size < 0.02)
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

            // todo why can this be null?
            if (rect == null)
                continue

            val expandedRect = expandRect(rect, ratioWidth = 0.1, ratioHeight = 0.5)
            if (expandedRect.width > expandedRect.height) {
                yield(expandedRect)
            }
        }
    }

    private fun prepareColourHeatmap(
        charHeatmap: Array<Float>,
        linkHeatmap: Array<Float>,
        resizeConfig: ResizeConfig,
    ): Bitmap {

        val combined = charHeatmap.zip(linkHeatmap).map { (char, link) -> max(char, link) }
        val colourHeatmap = makeColourHeatmap(combined, model.inputWidth, model.inputHeight)
        return undoResizeWithPadding(colourHeatmap, resizeConfig)
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
