package com.example.domesticviolencealert

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_need_help.view.*

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_need_help, container, false)
        view.report_button.setOnClickListener {
            sendEmail()
        }
        return view
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("domestic_violence_alert_need_help@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "From ${view!!.name.text}")
        intent.putExtra(Intent.EXTRA_TEXT, generateMessage())
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun generateMessage(): String  = activity!!.getString(R.string.need_help_message,
            view!!.phone_number.text.toString(),
            view!!.email_address.text.toString(),
            view!!.what_happened.text.toString())


}