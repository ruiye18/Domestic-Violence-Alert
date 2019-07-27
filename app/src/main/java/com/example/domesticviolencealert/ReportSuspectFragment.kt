package com.example.domesticviolencealert

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_report_form.view.*
import kotlinx.android.synthetic.main.fragment_report_form.view.email_address
import kotlinx.android.synthetic.main.fragment_report_form.view.header_home_button
import kotlinx.android.synthetic.main.fragment_report_form.view.phone_number
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.tasks.Continuation
import com.google.common.math.IntMath.mod
import kotlin.collections.ArrayList
import kotlin.random.Random


class ReportSuspectFragment : Fragment(),
    UploadProofTask.UploadConsumer{

    private var currentPhotoPath = ""

    private var proofsUrl = ArrayList<String>()
    private var proofsBitmap = ArrayList<Bitmap>()
    private var clickedOnProof = -1


    private val storageRef = FirebaseStorage
        .getInstance()
        .reference
        .child("images")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_form, container, false)
        view.header_home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
        }

        view.proof1.setOnClickListener {
            clickedOnProof = 0
            showPictureDialog()
        }
        view.proof2.setOnClickListener {
            clickedOnProof = 1
            showPictureDialog()
        }
        view.proof3.setOnClickListener {
            clickedOnProof = 2
            showPictureDialog()
        }

        //TODO: remove records part

        view.report_button.setOnClickListener {
            //main info
            val nameText = view.name.text.toString()
            val phoneText = view.phone_number.text.toString()
            val emailText = view.email_address.text.toString()

            //proof images
            for(bitmap in proofsBitmap) {
                storageAdd(nameText, phoneText, emailText, bitmap)
            }
        }

        return view
    }


    private fun storageAdd(name: String, phone: String, email: String, bitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val id = Math.abs(Random.nextLong()).toString()

        var uploadTask = storageRef.child(id).putBytes(data)
        uploadTask.addOnFailureListener {
            Log.d(Constants.TAG, "Image upload failed: $id")
        }.addOnSuccessListener {
            Log.d(Constants.TAG, "Image upload succeeded: $id")
        }

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageRef.child(id).downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                proofsUrl.add(downloadUri.toString())
                Log.d(Constants.TAG, "Image download URL succeeded: $downloadUri")

                //TODO: loading ?
                if (proofsUrl.size == proofsBitmap.size) {
                    val newSuspect = Suspect(phone, email, name, proofsUrl, ArrayList<Report>(), 50)
                    Utils.addSuspect(newSuspect)
                    Utils.switchFragment(context!!, MainInfoFragment.newInstance(newSuspect))
                }
            } else {
                Log.d(Constants.TAG, "Image download URL failed")
            }
        }
    }

    private fun showPictureDialog() {
        Log.d(Constants.TAG, "Adding proofs to $clickedOnProof")

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Upload Proofs")
        builder.setMessage("Note: only three proofs are allowed\nPlease choose the most intuitive ones")
        builder.setPositiveButton("Take Picture") { _, _ ->
            launchCameraIntent()
        }

        builder.setNegativeButton("Choose Picture") { _, _ ->
            launchChooseIntent()
        }
        builder.create().show()
    }

    private fun launchCameraIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.example.domesticviolencealert",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, Constants.RC_TAKE_PICTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun launchChooseIntent() {
        val choosePictureIntent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        choosePictureIntent.addCategory(Intent.CATEGORY_OPENABLE)
        choosePictureIntent.type = "image/*"
        if (choosePictureIntent.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(choosePictureIntent, Constants.RC_CHOOSE_PICTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.RC_TAKE_PICTURE -> {
                    sendCameraPhotoToTask()
                }
                Constants.RC_CHOOSE_PICTURE -> {
                    sendGalleryPhotoToTask(data)
                }
            }
        }
    }

    private fun sendCameraPhotoToTask() {
        Log.d(Constants.TAG, "Sending to adapter this photo: $currentPhotoPath from camera")
        UploadProofTask(currentPhotoPath, context!!, this).execute()
    }

    private fun sendGalleryPhotoToTask(data: Intent?) {
        if (data != null && data.data != null) {
            val location = data.data!!.toString()
            Log.d(Constants.TAG, "Sending to adapter this photo: $location from gallery")
            UploadProofTask(location, context!!, this).execute()
        }
    }


    override fun onUploadCompleted(bitmap: Bitmap) {
        if (clickedOnProof >= proofsBitmap.size) {
            clickedOnProof = proofsBitmap.size
            proofsBitmap.add(clickedOnProof, bitmap)
        } else {
            proofsBitmap[clickedOnProof] = bitmap
        }

        when(clickedOnProof) {
            0 ->  view!!.proof1.setImageBitmap(bitmap)
            1 ->  view!!.proof2.setImageBitmap(bitmap)
            2 ->  view!!.proof3.setImageBitmap(bitmap)
        }
    }


}