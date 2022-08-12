package io.krasch.openread.models

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.scale
import io.krasch.openread.geometry.algorithms.calculateConvexHull
import io.krasch.openread.geometry.algorithms.calculateMinAreaRectangle
import io.krasch.openread.geometry.algorithms.findConnectedComponents
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Array2D
import io.krasch.openread.geometry.types.Point
import io.krasch.openread.image.resize2
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.MappedByteBuffer
import kotlin.math.floor

const val THRESHOLD_TEXT_FIRST_PASS = 0.2
const val THRESHOLD_TEXT_SECOND_PASS = 0.8
const val THRESHOLD_LINK = 0.2

data class DetectionResult(
    val segmentation: List<Point>,
    val hull: List<Point>,
    val rectangle: AngledRectangle
)

val compatList = CompatibilityList()

class DetectionModel(modelFile: MappedByteBuffer) {

    val options = Interpreter.Options().apply {
        if (compatList.isDelegateSupportedOnThisDevice) {
            // if the device has a supported GPU, add the GPU delegate
            val delegateOptions = compatList.bestOptionsForThisDevice
            this.addDelegate(GpuDelegate(delegateOptions))
        } else {
            // if the GPU is not supported, run on 4 threads
            this.numThreads = 4
        }
    }

    val model = Interpreter(modelFile, options)

    fun run(bitmap: Bitmap): Pair<Bitmap, Sequence<DetectionResult>> {
        // preprocessing
        val (_, inputHeight, inputWidth, _) = model.getInputTensor(0).shape()
        val (resizeRatio, resizedBitmap) = resize2(bitmap, inputWidth, inputHeight)

        // model prediction
        val (charHeatmap, linkHeatmap) = predict(resizedBitmap)
        val heatmapImage = makeColourHeatmap(charHeatmap, 1 / resizeRatio)

        val boxes = postprocess(charHeatmap, linkHeatmap, 1 / resizeRatio)
        return Pair(heatmapImage, boxes)
    }

    private fun predict(bitmap: Bitmap): Pair<Array<Float>, Array<Float>> {
        // prepare input buffer
        val inputBuffer = allocateByteBuffer(model.getInputTensor(0))
        bitmapToByteBuffer(bitmap, inputBuffer)

        // prepare output buffers
        val charHeatmapBuffer = allocateByteBuffer(model.getOutputTensor(0))
        val linkHeatmapBuffer = allocateByteBuffer(model.getOutputTensor(1))

        // run model
        Log.v("bla", "detection model run start")
        model.runForMultipleInputsOutputs(
            arrayOf(inputBuffer),
            mapOf(0 to charHeatmapBuffer, 1 to linkHeatmapBuffer)
        )
        Log.v("bla", "detection model run done")

        // parse output buffers
        val charHeatmap = byteBufferToFloatArray(charHeatmapBuffer)
        val linkHeatmap = byteBufferToFloatArray(linkHeatmapBuffer)

        return Pair(charHeatmap, linkHeatmap)
    }

    private fun postprocess(
        charHeatmap: Array<Float>,
        linkHeatmap: Array<Float>,
        resizeRatio: Double
    ) = sequence {

        val (_, heatmapHeight, heatmapWidth, _) = model.getOutputTensor(0).shape()

        // boolean array where True = this pixel is likely part of a piece of text
        val isText = charHeatmap.zip(linkHeatmap).map { (charScore, linkScore) ->
            (charScore >= THRESHOLD_TEXT_FIRST_PASS) || (linkScore >= THRESHOLD_LINK)
        }

        // to find connected components we need a 2D representation of the array
        val isText2D = Array2D(isText.toTypedArray(), heatmapHeight, heatmapWidth)

        // also need the char heatmap as a 2D array to do the second thresholding step
        val charHeatmap2D = Array2D(charHeatmap, heatmapHeight, heatmapWidth)

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

            // todo why can this be null?
            if (rect != null)
                yield(DetectionResult(scaledPoints, hull, rect))
        }
    }

    private fun makeColourHeatmap(charHeatmap: Array<Float>, resizeRatio: Double): Bitmap {
        val (_, height, width, _) = model.getOutputTensor(0).shape()

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


        val pixels = charHeatmap.map {
            val colourId = floor(it * colourMap.size).toInt()
            colourMap[colourId]
        }

        val heatmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        heatmapImage.setPixels(pixels.toIntArray(), 0, width, 0, 0, width, height)

        return heatmapImage.scale(
            (width * resizeRatio).toInt(),
            (height * resizeRatio).toInt()
        )
    }
}
