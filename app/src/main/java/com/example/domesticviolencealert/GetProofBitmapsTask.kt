package com.example.domesticviolencealert

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log

class GetProofBitmapsTask(private var proofConsumer: ProofConsumer) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urlStrings: String?): Bitmap? {
        val inStream = java.net.URL(urlStrings[0]).openStream()
        return try {
            val bitmap = BitmapFactory.decodeStream(inStream)
            Log.d(Constants.TAG, "Loading image at $urlStrings ...")
            bitmap
        } catch (e: Exception) {
            Log.e(Constants.TAG, "EXCEPTION: " + e.toString())
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        proofConsumer.onProofLoaded(result)
        Log.d(Constants.TAG, "!!! finish loading image ")
    }

    interface ProofConsumer {
        fun onProofLoaded(bitmap: Bitmap?)
    }
}
