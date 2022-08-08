package io.krasch.openread

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry

fun getContext(): Context {
    return InstrumentationRegistry.getInstrumentation().context
}

fun getTargetContext(): Context {
    return InstrumentationRegistry.getInstrumentation().targetContext
}

fun readBitmap(filename: String): Bitmap {
    val context = getContext()
    val bitmap = BitmapFactory.decodeStream(context.resources.assets.open(filename))
    return bitmap!!
}

fun createBitmap(width: Double, height: Double, color: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(color)
    return bitmap
}
