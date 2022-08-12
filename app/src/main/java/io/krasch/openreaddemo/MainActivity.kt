package io.krasch.openreaddemo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import io.krasch.openread.OCR
import io.krasch.openreaddemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: OpenreadViewModel by viewModels()
    private lateinit var currentImage: Bitmap

    @Suppress("DEPRECATION")
    private fun getBitmapFromURI(uri: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                uri
            )
            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(
                    source,
                    ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    })
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load models to enable viewModel to do its job
        val ocr = OCR(context = this)
        viewModel.setOCR(ocr)

        // whenever the view model has some new ocr results, update the UI
        viewModel.results.observe(this, Observer { ocrResults ->
            ocrResults.let {
                val annotatedImage = drawOCRResults(currentImage, ocrResults)
                binding.imageView.setImageBitmap(annotatedImage)
            }
        })

        // when OCR is starting: update UI and trigger the models
        fun runOCR(bitmap: Bitmap?){
            bitmap?.run {
                currentImage = bitmap
                binding.imageView.setImageBitmap(bitmap)
                viewModel.triggerOCR(bitmap)
            }
        }

        // when the user clicks the button and selects an image, trigger the OCR
        val pickImage = registerForActivityResult(PickImageResultContract()) { uri ->
            uri?.run {
                val bitmap = getBitmapFromURI(this)
                runOCR(bitmap)
            }
        }
        binding.button.setOnClickListener { pickImage.launch(0) }

        // automatically trigger OCR (for testing purposes)
        //val bitmap = BitmapFactory.decodeStream(assets.open("seelowen.jpg"))
        //runOCR(bitmap)

    }
}


/*
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /*private val changeObserver = Observer<Any> { value ->
        value?.let {
            Log.v("bla", it.toString())
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*//liveDataA.observe(this, changeObserver)
        val model = ViewModelProvider(this).get(TextViewModel::class.java)

        //val model: TextViewModel by viewModels()
        //model.readTitle.observe(this, changeObserver)
        val result = model.trigger()
        result.observe(this, changeObserver)
        //model.liveDataA.postValue("test")*/
    }
}*/