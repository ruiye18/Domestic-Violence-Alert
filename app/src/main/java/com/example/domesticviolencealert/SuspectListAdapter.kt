package com.example.domesticviolencealert

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup


class SuspectListAdapter(var context: Context?, var listener: SuspectListFragment.OnSuspectSelectedListener?)
    : RecyclerView.Adapter<SuspectViewHolder>() {

    var suspects = ArrayList<Suspect>()

    init {
        suspects = Utils.loadSuspects()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuspectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_suspect, parent, false)
        return SuspectViewHolder(view, this)
    }

    override fun onBindViewHolder(holder: SuspectViewHolder, position: Int) {
        holder.bind(suspects[position])
    }

    override fun getItemCount() = suspects.size

    fun selectSuspectAt(adapterPosition: Int) {
        val suspect = suspects[adapterPosition]
        listener?.onSuspectSelected(suspect)
    }
}