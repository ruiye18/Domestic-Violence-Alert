package com.example.domesticviolencealert

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_suspect.view.*
import kotlinx.android.synthetic.main.row_view_suspect.view.suspect_name

class SuspectViewHolder(itemView: View, adapter: SuspectListAdapter) : RecyclerView.ViewHolder(itemView) {

    private val nameTextView = itemView.suspect_name as TextView
    private val scoreTextView = itemView.suspect_score as TextView
    private val scoreProcess = itemView.suspect_color as ImageView

    init {
        itemView.setOnClickListener{
            adapter.selectSuspectAt(adapterPosition)
        }
    }

    fun bind(suspect: Suspect) {
        nameTextView.text = suspect.name
        scoreTextView.text = suspect.score.toString()
        val colorGreen = 255 - suspect?.score!!.toInt() * 2
        scoreProcess.setColorFilter(Color.rgb(255, colorGreen, 0))
    }
}