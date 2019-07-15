package com.example.domesticviolencealert

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_report.view.*


class ReportViewHolder(itemView: View, adapter: AdditionalInfoListAdapter) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.report_title as TextView

    init {
        itemView.setOnClickListener{
            adapter.selectReportAt(adapterPosition)
        }
    }

    fun bind(report: Report) {
        titleTextView.text = report.title
    }
}