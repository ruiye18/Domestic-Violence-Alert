package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search.view.home_button
import kotlinx.android.synthetic.main.fragment_suspect_list.view.*

class SearchFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(Constants.TAG, "Enter search frag")
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        view.home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
        }

        view.search_go_button.setOnClickListener {
            val phone = view.search_phone_number.text.toString()
            val email = view.search_email.text.toString()
            val name = view.search_name.text.toString()
            searchForResults(phone, email, name)
        }

        return view
    }

//    private fun switchFragment(fragment: Fragment) {
//        val ft = activity!!.supportFragmentManager.beginTransaction()
//        ft.replace(R.id.fragment_container, fragment)
//        ft.addToBackStack("detail")
//        ft.commit()
//    }

    private fun searchForResults(phone: String, email: String, name: String) {
        Log.d(Constants.TAG, "Searching results for $phone and $email and $name")

        // TODO: pass suspect results to frag

        Utils.switchFragment(context!!, SuspectListFragment())
//        val ft = activity!!.supportFragmentManager.beginTransaction()
//        ft.replace(R.id.fragment_container, SuspectListFragment())
//        ft.addToBackStack("detail")
//        ft.commit()
    }

}