package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


private const val ARG_REPORT = "report"

class ReportDetailFragment : Fragment() {
    private var report: Report? = null

    companion object {
        @JvmStatic
        fun newInstance(report: Report) =
            ReportDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_REPORT, report)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            report = it.getParcelable(ARG_REPORT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_report, container, false)
        return view
    }


}