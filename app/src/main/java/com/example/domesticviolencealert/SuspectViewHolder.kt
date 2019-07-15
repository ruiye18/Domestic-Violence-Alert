package com.example.domesticviolencealert

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_suspect.view.*

class SuspectViewHolder(itemView: View, adapter: SuspectListAdapter) : RecyclerView.ViewHolder(itemView) {
    private val nameTextView = itemView.suspect_name as TextView

    init {
        itemView.setOnClickListener{
            adapter.selectSuspectAt(adapterPosition)
        }
    }

    fun bind(suspect: Suspect) {
        nameTextView.text = suspect.name
    }
}