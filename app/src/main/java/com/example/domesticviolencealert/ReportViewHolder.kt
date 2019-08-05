package com.example.domesticviolencealert

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_report.view.*


class ReportViewHolder(itemView: View, adapter: AdditionalInfoListAdapter, val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.report_title as TextView

    init {
        itemView.setOnClickListener{
            adapter.selectReportAt(adapterPosition)
        }
    }

    fun bind(report: Report) {
        titleTextView.text = report.title
        if (report.isCriminal) {
            titleTextView.setBackgroundColor(context.resources.getColor(R.color.helpRed))
        } else if (!report.agree) {
            titleTextView.setBackgroundColor(context.resources.getColor(R.color.disagreeRed))
        }
    }
}