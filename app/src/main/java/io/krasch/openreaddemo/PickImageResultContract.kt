package io.krasch.openreaddemo

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract


class PickImageResultContract : ActivityResultContract<Any, Bitmap?>() {
    lateinit var contentResolver: ContentResolver

    override fun createIntent(context: Context, irrelevantInput: Any): Intent {
        contentResolver = context.contentResolver
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?) : Bitmap? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }

        val uri = result?.data
        val bitmap = uri?.run { getBitmapFromURI(this) }
        return bitmap
    }

    @Suppress("DEPRECATION")
    private fun getBitmapFromURI(uri: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                uri
            )
            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(
                    source,
                    ImageDecoder.OnHeaderDecodedListener { decoder, _, _ ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    })
            }
        }
    }
}