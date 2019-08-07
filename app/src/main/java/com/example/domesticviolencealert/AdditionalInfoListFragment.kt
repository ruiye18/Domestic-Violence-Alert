package com.example.domesticviolencealert

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.*
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.header_home_button
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.tab_main_info
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.agree_button
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.disagree_button
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.personal_image
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.suspect_name
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.suspect_report_time
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


private const val ARG_SUSPECT = "suspect"

class AdditionalInfoListFragment : Fragment(), GetProofBitmapsTask.ProofConsumer {

    private var suspect: Suspect? = null
    private var listener: OnReportSelectedListener? = null
    private lateinit var adapter: AdditionalInfoListAdapter

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
            AdditionalInfoListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUSPECT, suspect)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            suspect = it.getParcelable(ARG_SUSPECT)
            Log.d(Constants.TAG, "Enter ${suspect?.name} additional info frag ")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addi_info_list, container, false)
        adapter = AdditionalInfoListAdapter(context, listener, suspect!!, suspect!!.reports)

        val recyclerView = view.addi_info_recycler_view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        view.header_home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
        }

        view.tab_main_info.setOnClickListener {
            Utils.switchFragment(context!!, MainInfoFragment.newInstance(suspect!!))
        }

        //header
        view.suspect_name.text = suspect?.name.toString()
        view.score_process_text_addi.text = suspect?.score!!.toString()
        (view.score_process_addi.layoutParams as LinearLayout.LayoutParams).weight = suspect?.score!!.toFloat()
        val colorGreen = 255 - suspect?.score!!.toInt() * 2
        view.score_process_color_addi.setBackgroundColor(Color.rgb(255,colorGreen,0))


        //update time
        var date = LocalDate.parse(suspect?.updateDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        var passDates = Utils.compareDates(date)
        var passDatesString = Utils.getPassDatesString(passDates, context!!)
        if (passDatesString.isEmpty()) {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            view.suspect_update_time.text = getString(R.string.latest_update, date.format(formatter).toString())
        } else {
            view.suspect_update_time.text = getString(R.string.latest_update, passDatesString)
        }

        //report time
        date = LocalDate.parse(suspect?.reportDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        passDates = Utils.compareDates(date)
        passDatesString = Utils.getPassDatesString(passDates, context!!)
        if (passDatesString.isEmpty()) {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            view.suspect_report_time.text = getString(R.string.created_on, date.format(formatter).toString())
        } else {
            view.suspect_report_time.text = getString(R.string.created_on, passDatesString)
        }

        view.add_report_button.setOnClickListener{
            Utils.switchFragment(context!!, AddReportFragment.newInstance(suspect!!))
        }

        if (suspect?.personalImage?.isNotEmpty()!!) {
            GetProofBitmapsTask(this).execute(suspect?.personalImage)
        }

        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnReportSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnReportSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnReportSelectedListener {
        fun onReportSelected(report: Report, suspect: Suspect)
    }

    override fun onProofLoaded(bitmap: Bitmap?) {
        view!!.personal_image.setImageBitmap(bitmap!!)
    }
}