package io.krasch.openread.tflite

import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Tensor
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

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

fun bitmapToByteBuffer(bitmap: Bitmap, buffer: ByteBuffer, convertToGreyscale: Boolean = false) {
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

        if (convertToGreyscale) {
            val grey = (red * 0.299f + green * 0.587f + blue * 0.114f) / 225f
            buffer.putFloat(grey)
        } else {
            buffer.putFloat(red)
            buffer.putFloat(green)
            buffer.putFloat(blue)
        }
    }
}

fun byteBufferToFloatArray(buffer: ByteBuffer): Array<Float> {
    val result = mutableListOf<Float>()

    buffer.rewind()
    while (buffer.hasRemaining())
        result.add(buffer.float)

    return result.toTypedArray()
}

fun byteBufferToInt64Array(buffer: ByteBuffer): Array<Float> {
    val result = mutableListOf<Float>()

    buffer.rewind()
    while (buffer.hasRemaining())
        result.add(buffer.long.toFloat()) // INT64 takes 8 bytes, same as long

    return result.toTypedArray()
}

fun byteBufferToArray(buffer: ByteBuffer, tensorType: DataType): Array<Float> {
    return if (tensorType == DataType.FLOAT32)
        byteBufferToFloatArray(buffer)
    else if (tensorType == DataType.INT64)
        byteBufferToInt64Array(buffer)
    else
        throw NotImplementedError("Unsupported tensor type ${tensorType.name}")
}

fun fileToByteBuffer(fileDescriptor: AssetFileDescriptor): MappedByteBuffer {
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)

    val buffer = inputStream.channel.map(
        FileChannel.MapMode.READ_ONLY,
        fileDescriptor.startOffset,
        fileDescriptor.declaredLength
    )

    inputStream.close()

    return buffer
}
