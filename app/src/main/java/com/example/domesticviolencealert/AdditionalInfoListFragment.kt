package com.example.domesticviolencealert

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.*
import kotlinx.android.synthetic.main.fragment_suspect_list.view.*

private const val ARG_SUSPECT = "suspect"

class AdditionalInfoListFragment : Fragment() {
    private var suspect: Suspect? = null
    private var listener: OnReportSelectedListener? = null

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
        val adapter = AdditionalInfoListAdapter(context, listener,suspect!!.reports)

        val recyclerView = view.addi_info_recycler_view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        view.header_home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, fragment)
//            ft.addToBackStack("detail")
//            ft.commit()
        }

        view.tab_main_info.setOnClickListener {
            Utils.switchFragment(context!!, MainInfoFragment.newInstance(suspect!!))
//            val fragment = MainInfoFragment.newInstance(suspect!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, fragment)
//            ft.addToBackStack("detail")
//            ft.commit()
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
        fun onReportSelected(report: Report)
    }

}