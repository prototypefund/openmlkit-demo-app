package io.krasch.openread.models

import android.graphics.Bitmap
import io.krasch.openread.image.resize
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer

const val FLOAT_SIZE = 4
const val INT64_SIZE = 8
const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz"

class RecognitionModel(modelFile: MappedByteBuffer, numThreads: Int = -1) {

    private val options = Interpreter.Options().setNumThreads(numThreads)
    val model = Interpreter(modelFile, options)

    fun predict(bitmap: Bitmap): String {
        val resized = resize(bitmap, 200, 31, true)

        val input = bitmapToByteBuffer(resized)
        val output = allocateOutputBuffer()

        // Log.v("bla", "detection model run start")
        // measurePerformance("recognition")  {model.run(input.rewind(), output.rewind())}
        model.run(input.rewind(), output.rewind())
        // Log.v("bla", "detection model run done")

        val indexes = (0 until 48).map { output.getInt(it * INT64_SIZE) }
        val chars = indexes.map { ALPHABET.getOrElse(it, { ' ' }) }
        val word = chars.toCharArray().concatToString().split(" ")[0]

        return word

        // return "test"
    }

    fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val width = bitmap.width
        val height = bitmap.height

        val buffer = ByteBuffer.allocateDirect(FLOAT_SIZE * width * height * 1)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val normed = pixels.map {
            val r = (it shr 16 and 0xFF)
            val g = (it shr 8 and 0xFF)
            val b = (it and 0xFF)

            // https://xjaphx.wordpress.com/2011/06/21/image-processing-grayscale-image-on-the-fly/
            val grey = r * 0.299f + g * 0.587f + b * 0.114f

            grey / 255f
        }

        for (i in 0 until width * height)
            buffer.putFloat(normed[i])

        return buffer
    }

    fun allocateOutputBuffer(): ByteBuffer {
        val outputShape = model.getOutputTensor(0).shape()
        val outputType = model.getOutputTensor(0).dataType()

        val outputSize = outputShape.reduce { acc, i -> acc * i } * 3

        val buffer = ByteBuffer.allocateDirect(outputSize * INT64_SIZE)
        buffer.order(ByteOrder.nativeOrder())

        return buffer
    }
}
