// CameraHelper.kt
package com.fit3163.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

class CameraHelper(private val activity: Activity) {

    private var photoUri: Uri? = null

    fun getCameraIntent(): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = File.createTempFile(
            "receipt_", ".jpg",
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        photoUri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.provider",
            photoFile
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        return intent
    }

    fun getPhotoUri(): Uri? = photoUri
}
