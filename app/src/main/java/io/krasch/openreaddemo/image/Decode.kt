package io.krasch.openreaddemo.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

@Suppress("DEPRECATION")
fun getBitmapFromURI(contentResolver: ContentResolver, uri: Uri): Bitmap {
    return when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
            contentResolver,
            uri
        )
        else -> {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(
                source,
                ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                })
        }
    }
}