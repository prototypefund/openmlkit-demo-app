package io.krasch.openreaddemo

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import io.krasch.openread.OCR
import io.krasch.openread.OCRResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.accumulate(): Flow<List<T>> = flow {
    val accumulated = mutableListOf<T>()
    collect {
        //Log.v("bla", it.toString())
        accumulated.add(it)
        emit(accumulated)
    }
}

class OpenreadViewModel() : ViewModel() {
    private lateinit var ocr: OCR

    private val trigger = MutableLiveData<Bitmap>()

    // todo move into factory?
    fun setOCR(ocr_: OCR) {
        ocr = ocr_
    }

    fun triggerOCR(bitmap: Bitmap) {
        // have already started the work on this image, no need to do it again
        if (bitmap.sameAs(trigger.value))
            return

        // start the work on this image
        trigger.value = bitmap
    }

    val results: LiveData<List<OCRResult>> = trigger.switchMap { image ->
        ocr.run(image)
            .asFlow()
            .flowOn(Dispatchers.Default)
            .accumulate()
            .asLiveData(viewModelScope.coroutineContext)
    }
}

/*
class OpenreadViewModel() : ViewModel() {

    private lateinit var ocr: OCR

    fun setOCR(ocr_: OCR){
        ocr = ocr_
    }

    fun runOCR(bitmap: Bitmap): Flow<List<OCRResult>> {
        return ocr.run(bitmap).asFlow().flowOn(Dispatchers.Default).accumulate()/*.stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = listOf()
        )*/
    }


 in activity use lifecycleScope.launch() {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.runOCR(square).collect {
                    val out = drawOCRResults(bitmap, it)
                    binding.imageView.setImageBitmap(out)
                }
            }
        }
}*/
