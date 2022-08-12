package io.krasch.openread

import android.content.Context
import android.graphics.Bitmap
import io.krasch.openread.geometry.types.expandRect
import io.krasch.openread.image.rotateAndCutout
import io.krasch.openread.models.DetectionModel
import io.krasch.openread.models.DetectionResult
import io.krasch.openread.models.RecognitionModel
import org.tensorflow.lite.support.common.FileUtil

data class OCRResult(
    val detection: DetectionResult,
    val text: String
)

class OCR(context: Context) {
    private val detection = DetectionModel(FileUtil.loadMappedFile(context, "craft-mini-126__epoch70_w720xh960.tflite"))
    private val recognition = RecognitionModel(FileUtil.loadMappedFile(context, "lite-model_keras-ocr_float16_2.tflite"), 4)

    fun run(bitmap: Bitmap): Sequence<OCRResult> {
        val (heatmap, detections) = detection.run(bitmap)

        val results = detections.map { box ->
            /*val expandedRect = expandRect(box.rectangle, 0.2)
            val cutoutImage = rotateAndCutout(bitmap, expandedRect)
            val word = recognition.predict(cutoutImage)

            if (word == "")
                OCRResult(box, "-")
            else
                OCRResult(box, word)*/

            OCRResult(box, "")
        }

        return results
    }
}
