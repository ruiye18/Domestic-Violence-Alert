package com.example.domesticviolencealert

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity



object Utils {
    fun loadSuspects(): ArrayList<Suspect> {
        val phones = arrayOf(
            "0000000000",
            "1111111111",
            "2222222222"
        )

        val emails = arrayOf(
            "example@email.com",
            "exampleTest@email.com",
            "exampleTestTest@email.com"
        )

        val names = arrayOf(
            "Suspect 1",
            "Suspect 2",
            "Suspect 3"
        )

        val report1 = ArrayList<Report>()
        report1.add(Report("Suspect 1 -> Report 1"))
        report1.add(Report("Suspect 1 -> Report 2"))
        report1.add(Report("Suspect 1 -> Report 3"))

        val report2 = ArrayList<Report>()
        report2.add(Report("Suspect 2 -> Report 1"))
        report2.add(Report("Suspect 2 -> Report 2"))
        report2.add(Report("Suspect 2 -> Report 3"))

        val report3 = ArrayList<Report>()
        report3.add(Report("Suspect 3 -> Report 1"))
        report3.add(Report("Suspect 3 -> Report 2"))
        report3.add(Report("Suspect 3 -> Report 3"))

        val reports = arrayOf (
            report1,
            report2,
            report3
        )

        val suspects = ArrayList<Suspect>()
        for (i in phones.indices) {
            val suspect = Suspect(phones[i], emails[i], names[i], reports[i],50)
            suspects.add(suspect)
        }

        return suspects
    }

    fun switchFragment(context: Context, fragment: Fragment) {
        val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        if (fragment != WelcomeFragment()) {
            ft.addToBackStack("detail")
        }
        ft.commit()
    }
}