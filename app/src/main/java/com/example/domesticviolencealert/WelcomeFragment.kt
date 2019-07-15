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
            switchFragment(SearchFragment())
        }
        //TODO: Report person + Need help

        return view
    }

    private fun switchFragment(fragment: Fragment) {
        val ft = activity!!.supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        while(activity!!.supportFragmentManager.backStackEntryCount > 0) {
            activity!!.supportFragmentManager.popBackStackImmediate()
        }
        ft.commit()
    }

}

