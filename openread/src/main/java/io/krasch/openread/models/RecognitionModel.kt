package io.krasch.openread.models

import android.content.Context
import android.graphics.Bitmap
import io.krasch.openread.image.resizeWithPadding
import io.krasch.openread.tflite.ImageModel

const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz"

const val RECOGNITION_MODEL_PATH = "lite-model_keras-ocr_float16_2.tflite"

class RecognitionModel(val model: ImageModel) {

    suspend fun run(bitmap: Bitmap): String {

        // preprocessing
        val (resized, _) = resizeWithPadding(bitmap, 200, 31, repeatEdge = true)

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

        // todo is it really a good idea to require the context here?
        suspend fun initialize(context: Context): RecognitionModel {
            val baseModel = ImageModel.initialize(
                RECOGNITION_MODEL_PATH,
                context,
                hasGPUSupport = false
            )
            return RecognitionModel(baseModel)
        }
    }
}
