package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.krasch.openreaddemo.databinding.ImageTabBinding

class ImageTabAdapter(private val tabNames: List<String>) : RecyclerView.Adapter<ImageHolder>() {

    private val nameToPosition = tabNames.withIndex().associate{ it.value to it.index }

    // initialize empty images
    private val images = Array<Bitmap?>(tabNames.size) { null }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = ImageTabBinding.inflate(
            LayoutInflater.from(parent.context) ,
            parent,
            false)
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun setImage(tabName: String, annotatedImage: Bitmap) {
        require(tabName in nameToPosition)
        val position = nameToPosition[tabName]!!

        images[position] = annotatedImage
        notifyItemChanged(position)
    }

    fun getTabName(position: Int): String {
        return tabNames[position]
    }
}
