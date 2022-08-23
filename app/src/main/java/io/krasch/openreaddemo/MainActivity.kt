package io.krasch.openreaddemo

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayoutMediator
import io.krasch.openreaddemo.databinding.ActivityMainBinding
import io.krasch.openreaddemo.image.PickImageResultContract
import io.krasch.openreaddemo.image.RecognitionResultsDrawer
import io.krasch.openreaddemo.image.getBitmapFromURI
import io.krasch.openreaddemo.tabs.ImageTab
import io.krasch.openreaddemo.tabs.ImageTabAdapter
import io.krasch.openreaddemo.tabs.disableAnimations


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

        // configure view pager
        disableAnimations(binding.viewPager)

        // wire tabs to view pager
        TabLayoutMediator(binding.imageTabs, binding.viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        // pre-select the "results" tab
        binding.viewPager.post {
            binding.viewPager.setCurrentItem(adapter.getTabPosition("results"), false)
        }

        viewModel.results.observe(this, Observer { results ->
            adapter.getImage("results")?.run {
                val drawer = RecognitionResultsDrawer(this)
                drawer.drawResults(results)
                //adapter.redrawTab("results")
                adapter.setImage("results", drawer.image)
            }
        })

        viewModel.image.observe(this, Observer { image ->
            adapter.setImage("original", image)
            adapter.setImage("results", image)

            binding.imageTabs.visibility = View.VISIBLE
            binding.viewPager.setCurrentItem(adapter.getTabPosition("results"), false)
        })

        viewModel.heatmap.observe(this, Observer { heatmap ->
            adapter.setImage("heatmap", heatmap)
        })

        viewModel.status.observe(this, Observer { status ->
            binding.statusBar.text = status
        })

        // when the user clicks the button and selects an image, trigger the OCR
        val pickImage = registerForActivityResult(PickImageResultContract()) { uri ->
            uri?.run {
                val bitmap = getBitmapFromURI(contentResolver, this)
                viewModel.triggerTextRecognition(bitmap)
            }
        }
        binding.pickImageButton.setOnClickListener { pickImage.launch(0) }
    }
}