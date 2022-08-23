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


data class TextRecognitionResult(
    val box: AngledRectangle,
    val text: String?
)

enum class Status {
    RUNNING,
    DONE,
}

data class RecognitionProgress (
    val total: Int,
    val done: Int
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
    val detectionStatus = MutableLiveData<Status>()
    val recognitionProgress = MutableLiveData<RecognitionProgress>()


    fun triggerTextRecognition(bitmap: Bitmap) {
        // have already started the work on this image, no need to do it again
        if (bitmap.sameAs(currentImage.value))
            return

        // start the work on this image
        currentImage.value = bitmap
        detectionStatus.value = Status.RUNNING
    }

    private val detectionResults = detectionModel.switchMap { model ->
        currentImage.switchMap { image ->
            liveData {
                emit(Pair(image, model.run(image)))
            }
        }
    }

    private val recognitionResults = recognitionModel.switchMap { model ->
        detectionResults.switchMap { (image, detections) ->

            if (image.sameAs(currentImage.value)) {
                detectionStatus.value = Status.DONE
            }

            liveData {
                val (heatmap, boxes) = detections

                val words = mutableListOf<String>()
                if (image.sameAs(currentImage.value)){
                    emit(Pair(image, boxes.zipWithDefault(words, null).map { TextRecognitionResult(it.first, it.second) }))
                    recognitionProgress.value = RecognitionProgress(detections.boxes.size, 0)
                }

                for (i in boxes.indices) {
                    val box = boxes[i]

                    if (!image.sameAs(currentImage.value))
                        break

                    val cutout = rotateAndCutout(image, box)
                    words.add(model.run(cutout))

                    if (!image.sameAs(currentImage.value))
                        break

                    recognitionProgress.value = RecognitionProgress(detections.boxes.size, i+1)
                    emit(Pair(image, boxes.zipWithDefault(words, null).map { TextRecognitionResult(it.first, it.second) }))
                }
            }
        }
    }

    val image: LiveData<Bitmap> = currentImage

    val heatmap = detectionResults.switchMap { (image, detections) ->
        liveData {
            if (image.sameAs(currentImage.value))
                emit(detections.heatmap)
        }
    }

    val results = recognitionResults.switchMap { (image, recognitions) ->
        liveData {
            if (image.sameAs(currentImage.value))
                emit(recognitions)
        }
    }

    val modelLoadingStatus = detectionModel.switchMap { _ ->
        recognitionModel.switchMap { _ ->
            liveData {
                emit(Status.DONE)
            }
        }
    }


    private fun loadModelFile(path: String): MappedByteBuffer {
        val context = getApplication<Application>().applicationContext
        return FileUtil.loadMappedFile(context, path)
    }
}