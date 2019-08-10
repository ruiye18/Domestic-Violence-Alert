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
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_add_report.view.*
import kotlinx.android.synthetic.main.fragment_add_report.view.back_button
import kotlinx.android.synthetic.main.fragment_add_report.view.info_body
import kotlinx.android.synthetic.main.fragment_add_report.view.report_image
import kotlinx.android.synthetic.main.fragment_add_report.view.report_title
import kotlinx.android.synthetic.main.fragment_report.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

private const val ARG_SUSPECT = "suspect"

class AddReportFragment: Fragment(),  UploadProofTask.UploadConsumer {
    private var suspect: Suspect? = null
    private var currentPhotoPath = ""
    private var currentBitmap : Bitmap? = null


    private val storageRef = FirebaseStorage
        .getInstance()
        .reference
        .child("reportImages")


    private var suspects = ArrayList<Suspect>()
    private val suspectsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.SUSPECTS_COLLECTION)

    private fun loadSuspects(): ArrayList<Suspect> {
        val suspects = ArrayList<Suspect>()
        suspectsRef
            .orderBy(Suspect.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e(Constants.TAG, "listen error: $exception")
                    return@addSnapshotListener
                } else {
                    for (docChange in snapshot!!.documentChanges) {
                        val suspect = Suspect.fromSnapshot(docChange.document)
                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                suspects.add(0, suspect)
                                Log.d(Constants.TAG, "Added suspect success with size ${suspects.size}")
                            }
                            DocumentChange.Type.MODIFIED -> {
                                val pos = suspects.indexOfFirst { suspect.id == it.id }
                                suspects[pos] = suspect
                            }
                        }
                    }
                }
            }
        return suspects
    }


    companion object {
        @JvmStatic
        fun newInstance(suspect: Suspect) =
            AddReportFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUSPECT, suspect)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            suspect = it.getParcelable(ARG_SUSPECT)
            loadSuspects()
            Log.d(Constants.TAG, "Enter add report for ${suspect?.name}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_report, container, false)
        view.progress_bar.visibility = View.GONE

        view.back_button.setOnClickListener {
            Utils.switchFragment(context!!, AdditionalInfoListFragment.newInstance(suspect!!))
        }

        view.add_report_image_button.setOnClickListener {
            showPictureDialog()
        }

        view.submit_button.setOnClickListener {
            if (currentBitmap == null) {
                addReport("")
            } else {
                view.progress_bar.visibility = View.VISIBLE

                storageAdd()
            }
        }

        return view
    }

    private fun storageAdd() {
        val baos = ByteArrayOutputStream()
        currentBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val id = Math.abs(Random.nextLong()).toString()

        var uploadTask = storageRef.child(id).putBytes(data)
        uploadTask.addOnFailureListener {
            Log.d(Constants.TAG, "Report Image upload failed: $id")
        }.addOnSuccessListener {
            Log.d(Constants.TAG, "Report Image upload succeeded: $id")
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
                Log.d(Constants.TAG, "Report Image download URL succeeded: $downloadUri")
                addReport(downloadUri.toString())
            } else {
                Log.d(Constants.TAG, "Report Image download URL failed")
            }
        }
    }

    private fun addReport(uri:String) {
        suspect?.updateDate = Utils.getCurrentDate()

        val title = view!!.report_title.text.toString()
        val info = view!!.info_body.text.toString()
        val isCriminal = view!!.is_criminal_toggle.isChecked
        val agree = view!!.agree_toggle.isChecked
        val addDate = Utils.getCurrentDate()


        Log.d(Constants.TAG, "$isCriminal")
        if (isCriminal) {
            suspect?.score = suspect?.score!! + 10
        }
        if (!agree) {
            suspect?.score = suspect?.score!! + 10
        } else {
            suspect?.score = suspect?.score!! - 10
        }

        if (suspect?.score!! < 0) {
            suspect?.score = 0
            suspectsRef.document(suspect!!.id).set(suspect as Any)
        }
        if (suspect?.score!! > 100) {
            suspect?.score = 100
            suspectsRef.document(suspect!!.id).set(suspect as Any)
        }

        suspect?.reports?.add(0, Report(title, info, isCriminal, uri, !agree, addDate))
        suspectsRef.document(suspect!!.id).set(suspect as Any)

        Utils.switchFragment(context!!, AdditionalInfoListFragment.newInstance(suspect!!))
    }

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.pic_dialog_title))
        builder.setMessage(getString(R.string.add_report_dialog))
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
        Log.d(Constants.TAG, "Reprot: Sending to task this photo: $currentPhotoPath from camera")
        UploadProofTask(currentPhotoPath, context!!, this).execute()
    }

    private fun sendGalleryPhotoToTask(data: Intent?) {
        if (data != null && data.data != null) {
            val location = data.data!!.toString()
            Log.d(Constants.TAG, "Report: Sending to task this photo: $location from gallery")
            UploadProofTask(location, context!!, this).execute()
        }
    }


    override fun onUploadCompleted(bitmap: Bitmap) {
        currentBitmap = bitmap
        view!!.report_image.setImageBitmap(bitmap)
    }

}