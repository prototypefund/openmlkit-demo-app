package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import io.krasch.openreaddemo.databinding.ImageTabBinding


class ImageTabAdapter(private val tabs: List<DrawableImageTab>) : RecyclerView.Adapter<ImageTabHolder>() {

    private val nameToPosition = tabs.withIndex().associate{ it.value.title to it.index }

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageTabHolder {
        val binding = ImageTabBinding.inflate(
            LayoutInflater.from(parent.context) ,
            parent,
            false)

        return ImageTabHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageTabHolder, position: Int) {
        holder.bind(tabs[position].getImage())
    }

    override fun getItemCount(): Int = tabs.size

    fun setImage(tabName: String, bitmap: Bitmap){
        val position = getTabPosition(tabName)
        tabs[position].setImage(bitmap)
        notifyItemChanged(position)
    }

    fun getCanvas(tabName: String): Canvas? {
        val position = getTabPosition(tabName)
        return tabs[position].getCanvas()
    }

    fun getTabTitle(position: Int): String {
        return tabs[position].title
    }

    fun getTabPosition(tabName: String): Int {
        require(tabName in nameToPosition)
        return nameToPosition[tabName]!!
    }

    fun redrawTab(tabName: String){
        val position = getTabPosition(tabName)
        notifyItemChanged(position)
    }

    override fun getItemId(position: Int): Long {
        return tabs[position].hashCode().toLong()
    }
}
