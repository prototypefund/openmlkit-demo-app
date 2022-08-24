package io.krasch.openreaddemo

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import io.krasch.openread.geometry.types.AngledRectangle
import io.krasch.openread.image.rotateAndCutout
import io.krasch.openread.models.DetectionModel
import io.krasch.openread.models.RecognitionModel
import io.krasch.openread.tflite.fileToByteBuffer
import java.nio.MappedByteBuffer


data class TextRecognitionResult(
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

    private val detectionModel = liveData {
        emit(DetectionModel.initialize(getApplication<Application>().applicationContext))
    }

    private val recognitionModel = liveData {
        emit(RecognitionModel.initialize(getApplication<Application>().applicationContext))
    }

    private val imageInternal = MutableLiveData<Bitmap>()
    private val statusInternal = MutableLiveData<String>()

    init {
        statusInternal.value = " "
    }


    fun triggerTextRecognition(bitmap: Bitmap) {
        // have already started the work on this image, no need to do it again
        if (bitmap.sameAs(imageInternal.value))
            return

        // start the work on this image
        imageInternal.value = bitmap
    }

    private val detectionResults = detectionModel.switchMap { model ->
        imageInternal.switchMap { image ->
            statusInternal.value = "Finding text..."

            liveData {
                emit(Pair(image, model.run(image)))
            }
        }
    }

    private val recognitionResults = recognitionModel.switchMap { model ->
        detectionResults.switchMap { (image, detections) ->

            statusInternal.value = " "

            liveData {
                val (heatmap, boxes) = detections

                val words = mutableListOf<String>()
                if (image.sameAs(imageInternal.value)) {
                    emit(
                        Pair(
                            image,
                            boxes.zipWithDefault(words, null)
                                .map { TextRecognitionResult(it.first, it.second) })
                    )
                }

                for (i in boxes.indices) {
                    val box = boxes[i]

                    if (!image.sameAs(imageInternal.value))
                        break

                    statusInternal.value = "Reading text (${i + 1}/${boxes.size})"

                    val cutout = rotateAndCutout(image, box)
                    words.add(model.run(cutout))

                    if (!image.sameAs(imageInternal.value))
                        break

                    emit(
                        Pair(
                            image,
                            boxes.zipWithDefault(words, null)
                                .map { TextRecognitionResult(it.first, it.second) })
                    )
                    if (i + 1 == boxes.size)
                        statusInternal.value = " "
                }
            }
        }
    }

    val image: LiveData<Bitmap> = imageInternal

    val status: LiveData<String> = statusInternal

    val heatmap = detectionResults.switchMap { (image, detections) ->
        liveData {
            if (image.sameAs(imageInternal.value))
                emit(detections.heatmap)
        }
    }

    val results = recognitionResults.switchMap { (image, recognitions) ->
        liveData {
            if (image.sameAs(imageInternal.value))
                emit(recognitions)
        }
    }
}