package com.example.domesticviolencealert

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.*
import kotlinx.android.synthetic.main.row_view_suspect.view.*
import kotlinx.android.synthetic.main.row_view_suspect.view.suspect_color
import kotlinx.android.synthetic.main.row_view_suspect.view.suspect_name

class SuspectViewHolder(itemView: View, adapter: SuspectListAdapter) : RecyclerView.ViewHolder(itemView),GetProofBitmapsTask.ProofConsumer {

    private val nameTextView = itemView.suspect_name as TextView
    private val scoreTextView = itemView.suspect_score as TextView
    private val scoreProcess = itemView.suspect_color as ImageView
    private val personalImageView = itemView.target_icon as ImageView

    init {
        itemView.setOnClickListener{
            adapter.selectSuspectAt(adapterPosition)
        }
    }

    fun bind(suspect: Suspect) {
        nameTextView.text = suspect.name
        scoreTextView.text = suspect.score.toString()
        val colorGreen = 255 - suspect.score.toInt() * 2
        scoreProcess.setColorFilter(Color.rgb(255, colorGreen, 0))


        if (suspect.personalImage.isNotEmpty()) {
            GetProofBitmapsTask(this).execute(suspect.personalImage)
        }
    }

    override fun onProofLoaded(bitmap: Bitmap?) {
        personalImageView.setImageBitmap(bitmap!!)
    }
}