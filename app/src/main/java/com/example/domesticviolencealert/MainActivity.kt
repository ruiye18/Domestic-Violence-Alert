package com.example.domesticviolencealert

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        SuspectListFragment.OnSuspectSelectedListener,
        AdditionalInfoListFragment.OnReportSelectedListener
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        if (savedInstanceState == null) {
            Utils.switchFragment(this, WelcomeFragment())
//            val fragment = WelcomeFragment()
//            val ft = supportFragmentManager.beginTransaction()
//            ft.add(R.id.fragment_container, fragment)
//            ft.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onSuspectSelected(suspect: Suspect) {
        Log.d(Constants.TAG, "selected: ${suspect.name}")
        Utils.switchFragment(this, MainInfoFragment.newInstance(suspect))
//        val suspectFragment = MainInfoFragment.newInstance(suspect)
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.fragment_container, suspectFragment)
//        ft.addToBackStack("detail")
//        ft.commit()
    }

    override fun onReportSelected(report: Report) {
        Log.d(Constants.TAG, "selected: ${report.title}")

        //TODO: ReportDetailFragment
    }
}
