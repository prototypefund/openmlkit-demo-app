package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import io.krasch.openreaddemo.databinding.ImageTabBinding

class ImageTabHolder(private val binding: ImageTabBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(image: Bitmap?) {
        // todo how often is this called
        // todo is this actually necessary?
        image?.let { binding.imageView.setImageBitmap(it) }
    }
}
