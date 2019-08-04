package com.example.domesticviolencealert

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*

class AdditionalInfoListAdapter (var context: Context?, var listener: AdditionalInfoListFragment.OnReportSelectedListener?,
                                 var reports: ArrayList<Report>) : RecyclerView.Adapter<ReportViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_report, parent, false)
        return ReportViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount() = reports.size

    fun selectReportAt(adapterPosition: Int) {
        val doc = reports[adapterPosition]
        listener?.onReportSelected(doc)
    }


}