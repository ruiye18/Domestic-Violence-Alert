package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_welcome.view.*

class WelcomeFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        Log.d(Constants.TAG,"Enter welcome frag")
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        view.search_button.setOnClickListener {
            Utils.switchFragment(context!!, SearchFragment())
        }

        view.report_button.setOnClickListener {
            Utils.switchFragment(context!!, ReportSuspectFragment())
        }

        //TODO: Login

        view.help_button.setOnClickListener {
            Utils.switchFragment(context!!, HelpFragment())
        }

        return view
    }

}

