package com.example.domesticviolencealert

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.support.media.ExifInterface
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.random.Random
import com.google.android.gms.tasks.Continuation


class UploadProofTask(private val localPath: String,
                      private val context: Context,
                      private val uploadConsumer: UploadConsumer) : AsyncTask<Void, Void, Bitmap>() {


    override fun doInBackground(vararg p0: Void?): Bitmap? {
        val ratio = 1
        return rotateAndScaleByRatio(context, localPath, ratio)
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)
        uploadConsumer.onUploadCompleted(result)
    }

    private fun rotateAndScaleByRatio(context: Context, localPath: String, ratio: Int): Bitmap? {
        return if (localPath.startsWith("content")) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(localPath))
            var exif: ExifInterface? = null
            try {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(localPath))!!
                exif = ExifInterface(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return rotateAndScaleBitmapByRatio(exif, bitmap, ratio)
        } else if (localPath.startsWith("/storage")) {
            val bitmap = BitmapFactory.decodeFile(localPath)
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(localPath)
            } catch (e: IOException) {
                Log.e(Constants.TAG, "Exif error: $e")
            }
            return rotateAndScaleBitmapByRatio(exif, bitmap, ratio)
        } else {
            null
        }
    }

    private fun rotateAndScaleBitmapByRatio(exif: ExifInterface?, bitmap: Bitmap, ratio: Int): Bitmap {
        val photoW = bitmap.width
        val photoH = bitmap.height
        val bm = Bitmap.createScaledBitmap(bitmap, photoW / ratio, photoH / ratio, true)

        val orientString = exif?.getAttribute(ExifInterface.TAG_ORIENTATION)
        val orientation =
            if (orientString != null) Integer.parseInt(orientString) else ExifInterface.ORIENTATION_NORMAL
        var rotationAngle = 0
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270

        val matrix = Matrix()
        matrix.setRotate(rotationAngle.toFloat(), bm.width.toFloat() / 2, bm.height.toFloat() / 2)

        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }


    interface UploadConsumer {
        fun onUploadCompleted(bitmap: Bitmap)
    }
}