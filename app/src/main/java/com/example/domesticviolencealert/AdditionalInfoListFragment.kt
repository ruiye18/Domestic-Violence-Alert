package com.example.domesticviolencealert

import android.content.Context
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


private const val ARG_SUSPECT = "suspect"

class AdditionalInfoListFragment : Fragment() {
    private var suspect: Suspect? = null
    private var listener: OnReportSelectedListener? = null
    lateinit var adapter: AdditionalInfoListAdapter

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
        adapter = AdditionalInfoListAdapter(context, listener,suspect!!.reports)

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

        //fab
        view.add_report_fab.setOnClickListener{
            showAddReportDialog()
        }

        return view
    }

    private fun showAddReportDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Add a report")

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
           val toAdd = Report("", "", false)
            adapter.add(toAdd)
            //TODO: switch to add report fragment
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.show()
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
        fun onReportSelected(report: Report)
    }

}