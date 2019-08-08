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
import kotlin.collections.ArrayList
import kotlin.random.Random


class ReportSuspectFragment : Fragment(), UploadProofTask.UploadConsumer{

    private var currentPhotoPath = ""
        private var proofs = arrayListOf(Proof("",""),Proof("",""),Proof("",""))
//    private var proofs = ArrayList<String>()
    private var proofsBitmap = ArrayList<Bitmap>()
    private var clickedOnProof = -1
    private var personalImageUri = ""
    private var personalImageBitmap: Bitmap? = null
    private val currentDate = Utils.getCurrentDate()

    private val storageRef = FirebaseStorage
        .getInstance()
        .reference
        .child("images")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_form, container, false)

        view.progress_bar.visibility = View.GONE

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

        view.personal_image.setOnClickListener{
            clickedOnProof = 10
            showPictureDialog()
        }

        view.report_button.setOnClickListener {
            //main info
            val nameText = view.name.text.toString()
            val phoneText = view.phone_number.text.toString()
            val emailText = view.email_address.text.toString()

            view.progress_bar.visibility = View.VISIBLE

            for(i in proofsBitmap.indices) {
                Log.d(Constants.TAG, "uploading proofs at #$i")
                storageAdd(nameText, phoneText, emailText, proofsBitmap[i], i)
            }
            when (clickedOnProof) {
                -1 -> {
                    val newSuspect = Suspect(phoneText, emailText, nameText, proofs, ArrayList<Report>(), 50, personalImageUri, currentDate, currentDate)
                    Utils.addSuspect(newSuspect)
                    Utils.switchFragment(context!!, WelcomeFragment())
                }
                10 -> {
                    Log.d(Constants.TAG, "uploading personal image")
                    storageAdd(nameText, phoneText, emailText, personalImageBitmap, 10)
                }
            }
        }

        return view
    }


    private fun storageAdd(name: String, phone: String, email: String, bitmap: Bitmap?, index: Int) {
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
                when (index) {
                    0 -> {
                        proofs[index] = Proof(view!!.proof1_text.text.toString(), downloadUri.toString())
                        Log.d(Constants.TAG, "Image download URL succeeded: $downloadUri")
                    }
                    1 -> {
                        proofs[index] = Proof(view!!.proof2_text.text.toString(), downloadUri.toString())
                        Log.d(Constants.TAG, "Image download URL succeeded: $downloadUri")
                    }
                    2 -> {
                        proofs[index] = Proof(view!!.proof3_text.text.toString(), downloadUri.toString())
                        Log.d(Constants.TAG, "Image download URL succeeded: $downloadUri")
                    }
                    10 -> {
                        personalImageUri = downloadUri.toString()
                        Log.d(Constants.TAG, "Image download URL succeeded: $downloadUri")
                    }
                }

                if (index == 10) {
                    val newSuspect = Suspect(phone, email, name, proofs, ArrayList<Report>(), 50, personalImageUri, currentDate, currentDate)
                    Utils.addSuspect(newSuspect)
                    Utils.switchFragment(context!!, WelcomeFragment())
                } else if (index + 1 == proofsBitmap.size && personalImageBitmap == null) {
                    val newSuspect = Suspect(phone, email, name, proofs, ArrayList<Report>(), 50, personalImageUri, currentDate, currentDate)
                    Utils.addSuspect(newSuspect)
                    Utils.switchFragment(context!!, WelcomeFragment())
                } else if (index + 1 == proofsBitmap.size && personalImageBitmap != null) {
                    storageAdd(name, phone, email, personalImageBitmap, 10)
                }

            } else {
                Log.d(Constants.TAG, "Image download URL failed")
            }
        }
    }

    private fun showPictureDialog() {
        Log.d(Constants.TAG, "Adding proofs to $clickedOnProof")

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.add_proofs_dialog_title))
        builder.setMessage(getString(R.string.add_proofs_dialog))
        builder.setPositiveButton(getString(R.string.pic_pos)) { _, _ ->
            launchCameraIntent()
        }
        builder.setNegativeButton(getString(R.string.pic_neg)) { _, _ ->
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
        if (clickedOnProof == 10) {
            personalImageBitmap = bitmap
            view!!.personal_image.setImageBitmap(bitmap)
        } else {
            if (clickedOnProof >= proofsBitmap.size) {
                clickedOnProof = proofsBitmap.size
                proofsBitmap.add(clickedOnProof, bitmap)
            } else {
                proofsBitmap[clickedOnProof] = bitmap
            }

            when (clickedOnProof) {
                0 -> view!!.proof1.setImageBitmap(bitmap)
                1 -> view!!.proof2.setImageBitmap(bitmap)
                2 -> view!!.proof3.setImageBitmap(bitmap)
            }
        }
    }
}