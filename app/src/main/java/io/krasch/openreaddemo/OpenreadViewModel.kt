package io.krasch.openreaddemo

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.image.rotateAndCutout
import io.krasch.openread.models.DetectionModel
import io.krasch.openread.models.RecognitionModel
import org.tensorflow.lite.support.common.FileUtil
import java.nio.MappedByteBuffer

const val DETECTION_MODEL_PATH = "craft-mini-126__epoch70_w720xh960.tflite"
const val RECOGNITION_MODEL_PATH = "lite-model_keras-ocr_float16_2.tflite"


data class OCRResult(
    val box: AngledRectangle,
    val text: String?
)


fun <T, R> List<T>.zipWithDefault(other: List<R>, default: R): List<Pair<T, R>> {
    require(this.size >= other.size)

    return this.indices.map {
        if (it < other.size)
            Pair(this[it], other[it])
        else
            Pair(this[it], default)
    }
}

class OpenreadViewModel(application: Application) : AndroidViewModel(application) {

    private val detectionModel = loadModelFile(DETECTION_MODEL_PATH).let {
        liveData {
            emit(DetectionModel.initialize(it))
        }
    }

    private val recognitionModel = loadModelFile(RECOGNITION_MODEL_PATH).let {
        liveData {
            emit(RecognitionModel.initialize(it))
        }
    }


    private val currentImage = MutableLiveData<Bitmap>()

    fun triggerOCR(bitmap: Bitmap) {
        // have already started the work on this image, no need to do it again
        if (bitmap.sameAs(currentImage.value))
            return

        // start the work on this image
        currentImage.value = bitmap
    }

    val detectionResults = detectionModel.switchMap { model ->
        currentImage.switchMap { image ->
            liveData {
                emit(Pair(image, model.run(image)))
            }
        }
    }

    val recognitionResults = recognitionModel.switchMap { model ->
        detectionResults.switchMap { (image, detections) ->
            liveData {
                val (heatmap, boxes) = detections

                val words = mutableListOf<String>()
                emit(Pair(image, boxes.zipWithDefault(words, null).map {OCRResult(it.first, it.second)}))

                for (box in boxes){
                    val cutout = rotateAndCutout(image, box)
                    words.add(model.run(cutout))

                    emit(Pair(image, boxes.zipWithDefault(words, null).map {OCRResult(it.first, it.second)}))
                }
            }
        }
    }

    private fun loadModelFile(path: String): MappedByteBuffer {
        val context = getApplication<Application>().applicationContext
        return FileUtil.loadMappedFile(context, path)
    }
}