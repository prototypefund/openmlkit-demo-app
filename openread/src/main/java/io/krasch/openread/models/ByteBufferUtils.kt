package io.krasch.openread.models

import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Tensor
import java.nio.ByteBuffer
import java.nio.ByteOrder

private fun getTypeSizeInBytes(tensorType: DataType): Int {
    return when (tensorType.name) {
        "FLOAT32" -> 4
        "INT64" -> 8
        else -> throw NotImplementedError("Unsupported tensor type ${tensorType.name}")
    }
}

fun allocateByteBuffer(tensor: Tensor): ByteBuffer {
    val tensorSize = tensor.shape().reduce { acc, i -> acc * i }
    val bufferSize = tensorSize * getTypeSizeInBytes(tensor.dataType())

    val buffer = ByteBuffer.allocateDirect(bufferSize)
    buffer.order(ByteOrder.nativeOrder())

    return buffer
}

fun bitmapToByteBuffer(bitmap: Bitmap, buffer: ByteBuffer) {
    // get a flat array of pixels
    val pixels = IntArray(bitmap.width * bitmap.height)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    // write them into buffer in channels_last order
    // for channels_first, you need write another function
    // that function should first put all red, then all green, then all blue
    pixels.map {
        val red = (it shr 16 and 0xFF).toFloat()
        val green = (it shr 8 and 0xFF).toFloat()
        val blue = (it and 0xFF).toFloat()

        buffer.putFloat(red)
        buffer.putFloat(green)
        buffer.putFloat(blue)
    }
}

fun byteBufferToFloatArray(buffer: ByteBuffer): Array<Float> {
    val result = mutableListOf<Float>()

    buffer.rewind()
    while (buffer.hasRemaining())
        result.add(buffer.float)

    return result.toTypedArray()
}
