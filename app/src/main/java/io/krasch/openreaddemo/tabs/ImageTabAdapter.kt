package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.krasch.openreaddemo.databinding.ImageTabBinding

class ImageTabAdapter(private val tabs: List<String>) : RecyclerView.Adapter<ImageTabHolder>() {

    private val images = MutableList<Bitmap?>(tabs.size) { null }
    private val nameToPosition = tabs.withIndex().associate { it.value to it.index }

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageTabHolder {
        val binding = ImageTabBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ImageTabHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageTabHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = tabs.size

    fun getImage(tabName: String): Bitmap? {
        val position = getTabPosition(tabName)
        return images[position]
    }

    fun setImage(tabName: String, bitmap: Bitmap) {
        val position = getTabPosition(tabName)
        images[position] = bitmap
        notifyItemChanged(position)
    }

    fun getTabTitle(position: Int): String {
        return tabs[position]
    }

    fun getTabPosition(tabName: String): Int {
        require(tabName in nameToPosition)
        return nameToPosition[tabName]!!
    }

    fun redrawTab(tabName: String) {
        val position = getTabPosition(tabName)
        notifyItemChanged(position)
    }

    override fun getItemId(position: Int): Long {
        return tabs[position].hashCode().toLong()
    }
}
