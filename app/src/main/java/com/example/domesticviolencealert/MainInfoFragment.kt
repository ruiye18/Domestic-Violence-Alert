package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_addi_info_list.view.*
import kotlinx.android.synthetic.main.fragment_main_info.view.*
import kotlinx.android.synthetic.main.fragment_main_info.view.header_home_button
import kotlinx.android.synthetic.main.fragment_main_info.view.tab_additional_info

private const val ARG_SUSPECT = "suspect"

class MainInfoFragment : Fragment(){
    private var suspect: Suspect? = null

    companion object {
        @JvmStatic
        fun newInstance(suspect: Suspect) =
            MainInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SUSPECT, suspect)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            suspect = it.getParcelable(ARG_SUSPECT)
            Log.d(Constants.TAG, "Enter ${suspect?.name} main info frag ")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_info, container, false)

        view.header_home_button.setOnClickListener {
            Utils.switchFragment(context!!, WelcomeFragment())
//            val fragment = WelcomeFragment()
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, fragment)
//            ft.addToBackStack("detail")
//            ft.commit()
        }

        view.tab_additional_info.setOnClickListener {
            Utils.switchFragment(context!!, AdditionalInfoListFragment.newInstance(suspect!!))
//            val fragment = AdditionalInfoListFragment.newInstance(suspect!!)
//            val ft = activity!!.supportFragmentManager.beginTransaction()
//            ft.replace(R.id.fragment_container, fragment)
//            ft.addToBackStack("detail")
//            ft.commit()
        }

        view.phone_number.text = context!!.getString(R.string.phone_number, suspect?.phone)
        view.email_address.text = context!!.getString(R.string.email_address, suspect?.email)
        return view
    }

}