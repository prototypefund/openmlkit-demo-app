package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import io.krasch.openreaddemo.databinding.ImageTabBinding


class ImageHolder(private val binding: ImageTabBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Bitmap?) {
        category?.let { image ->
            binding.imageView.setImageBitmap(image)
        }
    }
}