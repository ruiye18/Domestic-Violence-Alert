package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_report_form.view.*

class ReportSuspectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_form, container, false)
        view.header_home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
        }

        view.report_button.setOnClickListener {
            //main info
            val nameText = view.name.text.toString()
            val phoneText = view.phone_number.text.toString()
            val emailText = view.email_address.text.toString()

            //TODO: proofs
            val newSuspect = Suspect(phoneText, emailText, nameText, ArrayList<String>(), ArrayList<Report>(), 50)
            Utils.addSuspect(newSuspect)
            Utils.switchFragment(context!!, MainInfoFragment.newInstance(newSuspect))
        }


        return view
    }


}