package com.example.domesticviolencealert

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_main_info.view.*
import kotlinx.android.synthetic.main.fragment_main_info.view.header_home_button
import kotlinx.android.synthetic.main.fragment_main_info.view.tab_additional_info

private const val ARG_SUSPECT = "suspect"

class MainInfoFragment : Fragment(), GetProofBitmapsTask.ProofConsumer{
    private var suspect: Suspect? = null
    private var proofsBitmap = ArrayList<Bitmap>()

    var suspects = ArrayList<Suspect>()
    val suspectsRef = FirebaseFirestore
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
                }
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
        return suspects
    }

    companion object {
        @JvmStatic
        fun newInstance(suspect: Suspect) =
            MainInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUSPECT, suspect)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            suspect = it.getParcelable(ARG_SUSPECT)
            Log.d(Constants.TAG, "Enter ${suspect?.name} main info frag ")
            suspects = loadSuspects()
            if (suspect?.proofsImages?.isNotEmpty()!!) {
                for (url in suspect?.proofsImages!!) {
                    GetProofBitmapsTask(this).execute(url)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_info, container, false)

        view.header_home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
        }

        view.tab_additional_info.setOnClickListener {
            Utils.switchFragment(context!!, AdditionalInfoListFragment.newInstance(suspect!!))
        }

        //header
        view.suspect_name.text = suspect?.name.toString()
        view.score_process_text_main.text = suspect?.score!!.toString()
        (view.score_process_main.layoutParams as LinearLayout.LayoutParams).weight = suspect?.score!!.toFloat()
        val colorGreen = 255 - suspect?.score!!.toInt() * 2
        view.score_process_color_main.setBackgroundColor(Color.rgb(255,colorGreen,0))

        //main info
        view.phone_number.text = context!!.getString(R.string.phone_number, suspect?.phone)
        view.email_address.text = context!!.getString(R.string.email_address, suspect?.email)

        //TODO: agree/disagree hint + add report
        view.agree_button.setOnClickListener {
            showAgreeDialog()
        }
        view.disagree_button.setOnClickListener {
            showDisagreeDialog()
        }

        return view
    }

    private fun loadScore() {
        view!!.score_process_text_main.text = suspect?.score!!.toString()
        val params = LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT)
        params.weight = suspect?.score!!.toFloat()
        view!!.score_process_main.layoutParams = params
        val colorGreen = 255 - suspect?.score!!.toInt() * 2
        view!!.score_process_color_main.setBackgroundColor(Color.rgb(255,colorGreen,0))
    }

    private fun showDisagreeDialog() {
        suspect?.score = suspect?.score!! - 10
        suspectsRef.document(suspect!!.id).set(suspect as Any)
        loadScore()
    }

    private fun showAgreeDialog() {
        suspect?.score = 10 + suspect?.score!!
        suspectsRef.document(suspect!!.id).set(suspect as Any)
        loadScore()
    }

    override fun onProofLoaded(bitmap: Bitmap?) {
        proofsBitmap.add(bitmap!!)
        when(proofsBitmap.size) {
            1 ->  view!!.proof_image_1.setImageBitmap(bitmap)
            2 ->  view!!.proof_image_2.setImageBitmap(bitmap)
            3 ->  view!!.proof_image_3.setImageBitmap(bitmap)
        }
    }


}