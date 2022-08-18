package io.krasch.openread.models

import android.graphics.Bitmap
import io.krasch.openread.image.resizeRatio
import io.krasch.openread.tflite.ImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.MappedByteBuffer

const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz"

class RecognitionModel(val model: ImageModel) {

    suspend fun run(bitmap: Bitmap): String {
        // preprocessing
        val (resized, _) = resizeRatio(bitmap, 200, 31)

        // run model
        val charIndices = model.predict(resized)[0]

        // postprocessing
        return postprocess(charIndices)
    }

    private fun postprocess(charIndices: Array<Float>): String {
        // charIndices is an array of ALPHABET indices followed by a bunch of -1
        // -1 can be considered a STOP / FILL character
        // we are only interested in the characters before the first -1
        // (the result for this model will never contain a valid character after a -1 character)
        val stopPosition = charIndices.indexOfFirst { it <= -1 }
        val validCharIndices = charIndices.slice((0 until stopPosition))

        // for each character index, look up with letter this index maps to
        val chars = validCharIndices.map { ALPHABET[it.toInt()] }

        return String(chars.toCharArray())
    }

    companion object {
        suspend fun initialize(modelFile: MappedByteBuffer): RecognitionModel {
            val model = withContext(Dispatchers.IO) {
                ImageModel(modelFile, hasGPUSupport = false)
            }
            return RecognitionModel(model)
        }
    }
}
