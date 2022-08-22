package io.krasch.openreaddemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayoutMediator
import io.krasch.openreaddemo.databinding.ActivityMainBinding
import io.krasch.openreaddemo.image.PickImageResultContract
import io.krasch.openreaddemo.image.drawOCRResults
import io.krasch.openreaddemo.image.getBitmapFromURI
import io.krasch.openreaddemo.tabs.DrawableImageTab
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

        // have 3 image tabs, todo only the "results" one actually needs to be drawable
        val tabs = listOf(
            DrawableImageTab("original"),
            DrawableImageTab("results"),
            DrawableImageTab("heatmap")
        )

        // initialise tabs
        val adapter = ImageTabAdapter(tabs)
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
            adapter.getCanvas("results")?.run {
                drawOCRResults(this, results)
                adapter.redrawTab("results")
            }
        })

        viewModel.image.observe(this, Observer { image ->
            adapter.setImage("original", image)
            adapter.setImage("results", image)
            binding.imageTabs.visibility = View.VISIBLE
            binding.statusBar.text = " "
        })

        viewModel.heatmap.observe(this, Observer { heatmap ->
            adapter.setImage("heatmap", heatmap)
        })

        viewModel.detectionStatus.observe(this, Observer { status ->
            if (status == Status.RUNNING)
                binding.statusBar.text = "Finding text..."
            else
                binding.statusBar.text = " "
        })

        viewModel.recognitionProgress.observe(this, Observer { progress ->
            if ((progress.done < progress.total) && (progress.total > 0))
                binding.statusBar.text = "Reading text (${progress.done} / ${progress.total})"
            else
                binding.statusBar.text  = " "
        })

        // when the user clicks the button and selects an image, trigger the OCR
        val pickImage = registerForActivityResult(PickImageResultContract()) { uri ->
            uri?.run {
                val bitmap = getBitmapFromURI(contentResolver, this)
                viewModel.triggerOCR(bitmap)
            }
        }
        binding.pickImageButton.setOnClickListener { pickImage.launch(0) }


        // automatically trigger OCR (for testing purposes)
        //val bitmap = BitmapFactory.decodeStream(assets.open("seelowen.jpg"))
        //runOCR(bitmap)

    }
}