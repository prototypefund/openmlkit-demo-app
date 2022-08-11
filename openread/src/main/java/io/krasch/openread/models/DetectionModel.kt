package io.krasch.openread.models

import android.graphics.Bitmap
import android.util.Log
import io.krasch.openread.geometry.algorithms.calculateConvexHull
import io.krasch.openread.geometry.algorithms.calculateMinAreaRectangle
import io.krasch.openread.geometry.algorithms.findConnectedComponents
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.geometry.types.Array2D
import io.krasch.openread.geometry.types.Point
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer

const val IMAGE_SIZE = 640

data class DetectionResult(
    val segmentation: List<Point>,
    val hull: List<Point>,
    val rectangle: AngledRectangle
)

class DetectionModel(modelFile: MappedByteBuffer, numThreads: Int = -1) {

    private val options = Interpreter.Options().setNumThreads(numThreads)

    val model = Interpreter(modelFile, options)

    fun predict(bitmap: Bitmap) = sequence<DetectionResult> {
        val ratio = bitmap.width.toDouble() / IMAGE_SIZE
        val scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, false)

        val input = bitmapToByteBuffer(scaled)
        val charHeatmap = allocateOutputBuffer(0)
        val linkHeatmap = allocateOutputBuffer(1)

        Log.v("bla", "detection model run start")
        // measurePerformance("detection") {model.run(input, output)}
        model.runForMultipleInputsOutputs(arrayOf(input), mapOf(0 to charHeatmap, 1 to linkHeatmap))
        Log.v("bla", "detection model run done")

        val textScore = parseOutput(charHeatmap)
        val linkScore = parseOutput(linkHeatmap)

        val components = findConnectedComponents(textScore, linkScore)

        for (component in components) {
            val scaledComponent = component.map { Point(it.first * ratio, it.second * ratio) }
            val hull = calculateConvexHull(scaledComponent)
            val rect = calculateMinAreaRectangle(hull)
            if (rect != null)
                yield(DetectionResult(scaledComponent, hull, rect))
        }
    }

    fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val width = bitmap.width
        val height = bitmap.height

        val buffer = ByteBuffer.allocateDirect(FLOAT_SIZE * width * height * 3)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val normed = pixels.map {
            val r = (it shr 16 and 0xFF) // / 255f
            val g = (it shr 8 and 0xFF) // / 255f
            val b = (it and 0xFF) // / 255f

            /*val rNorm = (r - 0.485f) / 0.229f
            val gNorm = (g - 0.456f) / 0.224f
            val bNorm = (b - 0.406f) / 0.225f*/

            val rNorm = r.toFloat()
            val gNorm = g.toFloat()
            val bNorm = b.toFloat()

            Triple(rNorm, gNorm, bNorm)
        }

        // first all the reds, then all the greens, then all the blues
        // todo only tried with quadratic images
        /*for (i in 0 until width * height)
            buffer.putFloat(normed[i].first)
        for (i in 0 until width * height)
            buffer.putFloat(normed[i].second)
        for (i in 0 until width * height)
            buffer.putFloat(normed[i].third)*/
        for (i in 0 until width * height) {
            buffer.putFloat(normed[i].first)
            buffer.putFloat(normed[i].second)
            buffer.putFloat(normed[i].third)
        }

        return buffer
    }

    fun allocateOutputBuffer(outputIndex: Int): ByteBuffer {
        val outputShape = model.getOutputTensor(outputIndex).shape()
        val outputSize = outputShape.reduce { acc, i -> acc * i }

        val buffer = ByteBuffer.allocateDirect(outputSize * FLOAT_SIZE)
        buffer.order(ByteOrder.nativeOrder())

        return buffer
    }

    private fun parseOutput(result: ByteBuffer): Array2D<Float> {
        // Log.v("bla", model.getS)
        val shape = model.getOutputTensor(0).shape()
        val width = shape[1] // todo only tried with quadratic images
        val height = shape[2]

        val parsed = mutableListOf<Float>()

        result.rewind()
        while (result.hasRemaining()) {
            parsed.add(result.float)
        }

        return Array2D(parsed.toTypedArray(), width, height)
    }
}
