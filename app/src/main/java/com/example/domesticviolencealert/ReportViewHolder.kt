package com.example.domesticviolencealert

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_report.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit


class ReportViewHolder(itemView: View, adapter: AdditionalInfoListAdapter, val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.report_title as TextView
    private val dateTextView = itemView.report_date as TextView

    init {
        itemView.setOnClickListener{
            adapter.selectReportAt(adapterPosition)
        }
    }

    fun bind(report: Report) {
        titleTextView.text = report.title

        val date = LocalDate.parse(report.addDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        val passDates = Utils.compareDates(date)
        val passDatesString = Utils.getPassDatesString(passDates, context)
        if (passDatesString.isEmpty()) {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            dateTextView.text = date.format(formatter).toString()
        } else {
            dateTextView.text = passDatesString
        }

        if (report.isCriminal) {
            titleTextView.setBackgroundColor(context.resources.getColor(R.color.helpRed))
        } else if (!report.agree) {
            titleTextView.setBackgroundColor(context.resources.getColor(R.color.disagreeRed))
        }
    }
}