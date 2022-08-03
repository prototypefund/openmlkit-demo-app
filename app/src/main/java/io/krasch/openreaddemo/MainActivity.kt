package io.krasch.openreaddemo

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.krasch.openread.OCR
import io.krasch.openreaddemo.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var ocr: OCR
    private val context = this

    private fun runOCR(bitmap: Bitmap){
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                binding.loadingPanel.visibility = View.VISIBLE
                binding.button.isEnabled = false
                binding.imageView.setOnTouchListener(null)
            }

            if (!::ocr.isInitialized) // todo code smell?
                ocr = OCR(context)

            val result =ocr.run(bitmap)
            val out = drawOCRResults(bitmap, result)
            //val out = drawSegmentation(bitmap, result.map())

            runOnUiThread {
                binding.loadingPanel.visibility = View.GONE
                binding.imageView.setImageBitmap(out)
                binding.button.isEnabled = true
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val bitmap = BitmapFactory.decodeStream(assets.open("tears.jpg"))
        val square = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.width)
        binding.imageView.setImageBitmap(square)
        runOCR(bitmap)*/

        val pickImage = registerForActivityResult(PickImageResultContract()) {
            it?.run {
                val square = Bitmap.createBitmap(it, 0, 0, it.width, it.width)
                binding.imageView.setImageBitmap(square)
                runOCR(square)
            }
        }

        binding.button.setOnClickListener {pickImage.launch(0)}
        /*val bitmap = BitmapFactory.decodeStream(assets.open("seelöwen2.jpg"))
        val square = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.width)
        runOCR(square)*/
    }
}