package com.example.domesticviolencealert

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class SuspectInfoPagerAdapter (private val context: Context,
                               fm: FragmentManager,
                               private val suspect: Suspect) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        if (position == 1) {
            return AdditionalInfoListFragment.newInstance(suspect)
        }
        return MainInfoFragment.newInstance(suspect)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> return context.getString(R.string.main_information)
            1 -> return context.getString(R.string.additional_information)
        }
        return ""
    }

}