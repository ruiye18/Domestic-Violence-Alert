package com.example.domesticviolencealert

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_suspect_list.view.*

private const val ARG_SUSPECTS = "suspects"

class SuspectListFragment : Fragment(){

    private var listener: OnSuspectSelectedListener? = null
    lateinit private var suspects: ArrayList<Suspect>

    companion object {
        @JvmStatic
        fun newInstance(suspects: ArrayList<Suspect>) =
            SuspectListFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_SUSPECTS, suspects)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            suspects = it.getParcelableArrayList(ARG_SUSPECTS)
            Log.d(Constants.TAG, "suspects list frag with size ${suspects.size}")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_suspect_list, container, false)
        val adapter = SuspectListAdapter(suspects, context, listener)

        //buttons
        view.home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
        }
        view.back_button.setOnClickListener {
            Utils.switchFragment(context!!, SearchFragment())
        }

        //recycler view
        val recyclerView = view.recycler_view
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSuspectSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSuspectSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnSuspectSelectedListener {
        fun onSuspectSelected(suspect: Suspect)
    }
}