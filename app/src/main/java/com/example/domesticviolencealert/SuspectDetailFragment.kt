package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.suspect_header_main.view.*
import kotlinx.android.synthetic.main.suspect_header_main.view.home_button


private const val ARG_SUSPECT = "suspect"

class SuspectDetailFragment : Fragment(){
    private var suspect: Suspect? = null

    companion object {
        @JvmStatic
        fun newInstance(suspect: Suspect) =
            SuspectDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUSPECT, suspect)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            suspect = it.getParcelable(ARG_SUSPECT)
            Log.d(Constants.TAG, "Enter ${suspect?.name} detail frag")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.suspect_header_main, container, false)

        //header
        view.home_button.setOnClickListener {
            val ft = activity!!.supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragment_container, WelcomeFragment())
            while(activity!!.supportFragmentManager.backStackEntryCount > 0) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
        view.suspect_name.text = suspect?.name

//        //view pager
//        val sectionsPagerAdapter = SuspectInfoPagerAdapter(context!!, activity!!.supportFragmentManager, suspect!!)
//        val viewPager = view.view_pager
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs = view.tabs
//        tabs.setupWithViewPager(viewPager)

        //TODO: change view pager to frag container and tabs to buttons

        return view
    }

}