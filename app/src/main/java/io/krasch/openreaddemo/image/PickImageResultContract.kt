package io.krasch.openreaddemo.image

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract


class PickImageResultContract : ActivityResultContract<Any, Uri?>() {
    lateinit var contentResolver: ContentResolver

    override fun createIntent(context: Context, irrelevantInput: Any): Intent {
        contentResolver = context.contentResolver
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
        return intent
    }

    override fun parseResult(resultCode: Int, result: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }

        return result?.data
    }
}