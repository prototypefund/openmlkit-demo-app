package io.krasch.openreaddemo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayoutMediator
import io.krasch.openreaddemo.databinding.ActivityMainBinding
import io.krasch.openreaddemo.image.PickImageResultContract
import io.krasch.openreaddemo.image.drawOCRResults
import io.krasch.openreaddemo.image.getBitmapFromURI
import io.krasch.openreaddemo.tabs.ImageTabAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: OpenreadViewModel by viewModels()


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialise tabs
        val adapter = ImageTabAdapter(listOf("original", "results", "heatmap"))
        binding.viewPager.adapter = adapter

        // wire tabs to view pager
        TabLayoutMediator(binding.imageTabs, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabName(position)
        }.attach()


        // whenever the view model has some new ocr results, update the UI
        val annotatedImage = viewModel.recognitionResults.map { (image, ocrResults) ->
            drawOCRResults(image, ocrResults)
            //image
        }

        annotatedImage.observe(this, Observer { image ->
            adapter.setImage("results", image)
        })

        viewModel.currentImage.observe(this, Observer { image ->
            adapter.setImage("original", image)
        })

        viewModel.detectionResults.observe(this, Observer { (_, results) ->
            adapter.setImage("heatmap", results.heatmap)
        })

        // when OCR is starting: update UI and trigger the models
        fun runOCR(bitmap: Bitmap?){
            bitmap?.run {
                //binding.imageView.setImageBitmap(bitmap)
                viewModel.triggerOCR(bitmap)
            }
        }

        // when the user clicks the button and selects an image, trigger the OCR
        val pickImage = registerForActivityResult(PickImageResultContract()) { uri ->
            uri?.run {
                val bitmap = getBitmapFromURI(contentResolver, this)
                runOCR(bitmap)
            }
        }
        binding.pickImageButton.setOnClickListener { pickImage.launch(0) }

        // automatically trigger OCR (for testing purposes)
        //val bitmap = BitmapFactory.decodeStream(assets.open("seelowen.jpg"))
        //runOCR(bitmap)

    }
}