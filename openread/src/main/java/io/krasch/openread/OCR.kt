package io.krasch.openread

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import io.krasch.openread.models.RecognitionModel
import org.tensorflow.lite.support.common.FileUtil

data class OCRResult(
    val detection: DetectionResult,
    val text: String
)

class OCR(private val context: Context) {
    private val detection = DetectionModel(FileUtil.loadMappedFile(context, "craft-mini-116__epoch18.tflite"), 4)
    private val recognition = RecognitionModel(FileUtil.loadMappedFile(context, "lite-model_keras-ocr_float16_2.tflite"), 4)

    fun run(bitmap: Bitmap): List<OCRResult> {
        val detections = detection.predict(bitmap)
        Log.v("bla", "detection done")

        val result = detections.withIndex().map { (idx, det) ->
            // val expandedRect = expandRect(det.rectangle, 0.2)
            /*val cut = rotateAndCutout(bitmap, det.rectangle)

            var word = recognition.predict(cut)
            Log.v("bla", word)
            if (word == "")
                word = "-"

            Log.v("bla", "$idx $word")
            //val scaled = resize(cut, 200, 31, true)
            //writeBitmap(context, cut, "$idx.jpg")*/

            OCRResult(det, "-")
        }

        return result

        // return listOf<OCRResult>()
    }
}
