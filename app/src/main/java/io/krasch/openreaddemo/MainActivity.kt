package io.krasch.openreaddemo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import io.krasch.openread.OCR
import io.krasch.openreaddemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val context = this

    private lateinit var ocr: OCR
    private val viewModel: OpenreadViewModel by viewModels()

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

        // whenever the view model has some new ocr results, update the UI
        viewModel.recognitions.observe(this, Observer { (image, ocrResults) ->
            ocrResults.let {
                val annotatedImage = drawOCRResults(image, ocrResults)
                binding.imageView.setImageBitmap(annotatedImage)
            }
        })

        // when OCR is starting: update UI and trigger the models
        fun runOCR(bitmap: Bitmap?){
            bitmap?.run {
                binding.imageView.setImageBitmap(bitmap)

                // todo this should be nicer
                // do not want to load models right at the beginning because then app slow
                if (!::ocr.isInitialized){
                    ocr = OCR(context)
                    viewModel.setOCR(ocr)
                }

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