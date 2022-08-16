package io.krasch.openreaddemo

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import io.krasch.openread.OCR
import io.krasch.openread.OCRResult


class OpenreadViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var ocr: OCR

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val image = MutableLiveData<Bitmap>()

    // todo move into factory?
    fun setOCR(ocr_: OCR) {
        ocr = ocr_
    }

    fun triggerOCR(bitmap: Bitmap) {
        // have already started the work on this image, no need to do it again
        if (bitmap.sameAs(image.value))
            return

        // start the work on this image
        image.value = bitmap
    }

    val detections = image.switchMap { image ->
        liveData {
            emit(Pair(image, ocr.detection.run(image)))
        }
    }

    val recognitions = detections.switchMap { (image, boxes) ->
        liveData {
            mutableListOf<OCRResult>().apply {
                for (box in boxes) {
                    val word = ocr.recognition.run(image, box)
                    this.add(OCRResult(box, word))
                    emit(Pair(image, this.toList()))
                }
            }
        }
    }
}