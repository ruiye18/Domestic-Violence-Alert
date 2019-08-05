package com.example.domesticviolencealert

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main_info.view.*
import kotlinx.android.synthetic.main.fragment_report.view.*


private const val ARG_REPORT = "report"
private const val ARG_SUSPECT = "suspect"

class ReportDetailFragment : Fragment(), GetProofBitmapsTask.ProofConsumer {
    private var report: Report? = null
    private var suspect: Suspect? = null

    companion object {
        @JvmStatic
        fun newInstance(report: Report, suspect: Suspect) =
            ReportDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_REPORT, report)
                    putParcelable(ARG_SUSPECT, suspect)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            report = it.getParcelable(ARG_REPORT)
            suspect = it.getParcelable(ARG_SUSPECT)
            GetProofBitmapsTask(this).execute(report!!.reportImage)

            Log.d(Constants.TAG, "Enter report detail frag for ${report?.title} in ${suspect?.name}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_report, container, false)
        view.report_title.text = report!!.title
        view.info_body.text = report!!.info

        view.back_button.setOnClickListener {
            Utils.switchFragment(context!!, AdditionalInfoListFragment.newInstance(suspect!!))
        }

        return view
    }

    override fun onProofLoaded(bitmap: Bitmap?) {
        view?.report_image?.setImageBitmap(bitmap)
    }

}