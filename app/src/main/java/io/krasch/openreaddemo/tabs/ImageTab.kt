package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import android.graphics.Canvas

class DrawableImageTab(val title: String){
    private lateinit var image: Bitmap
    private lateinit var canvas: Canvas

    fun setImage(image: Bitmap) {
        this.image = image.copy(Bitmap.Config.ARGB_8888, true)
        this.canvas = Canvas(this.image)
    }

    fun getCanvas(): Canvas? {
        return if (this::canvas.isInitialized) { // todo
            this.canvas
        } else {
            null
        }
    }

    fun getImage(): Bitmap? {
        return if (this::image.isInitialized) { // todo
            this.image
        } else {
            null
        }
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrawableImageTab

        if (title != other.title) return false

        return true
    }
}
