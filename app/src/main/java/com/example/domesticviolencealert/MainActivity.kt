package com.example.domesticviolencealert

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        SuspectListFragment.OnSuspectSelectedListener,
        AdditionalInfoListFragment.OnReportSelectedListener
{

    private val WRITE_EXTERNAL_STORAGE_PERMISSION = 2
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        checkPermissions()

        //TODO: phone auth
        val user = auth.currentUser
        if (user != null) {
            Utils.switchFragment(this, WelcomeFragment())
        } else {
            signInAnonymously()
        }
    }

    private fun signInAnonymously() {
        auth.signInAnonymously().addOnSuccessListener(this) {
            Utils.switchFragment(this, WelcomeFragment())
        }.addOnFailureListener(this) { e ->
            Log.e(Constants.TAG, "signInAnonymously:FAILURE", e)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat
                .checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION
            )
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
    }

    override fun onReportSelected(report: Report) {
        Log.d(Constants.TAG, "selected: ${report.title}")

        //TODO: ReportDetailFragment
    }
}
