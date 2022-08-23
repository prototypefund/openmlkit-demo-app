package io.krasch.openreaddemo.tabs

import android.graphics.Bitmap
import android.graphics.Canvas

class ImageTab(val title: String){
    var image: Bitmap? = null

    override fun hashCode(): Int {
        return title.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageTab

        if (title != other.title) return false

        return true
    }
}
