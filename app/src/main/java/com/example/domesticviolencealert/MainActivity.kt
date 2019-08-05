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
import com.firebase.ui.auth.AuthUI


import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        SuspectListFragment.OnSuspectSelectedListener,
        AdditionalInfoListFragment.OnReportSelectedListener
//        SplashFragment.OnLoginButtonPressedListener
{

    private val WRITE_EXTERNAL_STORAGE_PERMISSION = 2
    private val RC_SIGN_IN = 1

    private val auth = FirebaseAuth.getInstance()
    lateinit var authStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        checkPermissions()
//        initializeListeners()
        val user = auth.currentUser
        if (user != null) {
            Utils.switchFragment(this, WelcomeFragment())
        } else {
            signInAnonymously()
        }
    }

//    private fun initializeListeners() {
//        authStateListener = FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
//            val user = auth.currentUser
//            Log.d(Constants.TAG, "In auth listener, user = $user")
//            if (user != null) {
//                Log.d(Constants.TAG, "UID: ${user.uid}")
//                Log.d(Constants.TAG, "Phone: ${user.phoneNumber}")
//                Utils.switchFragment(this, WelcomeFragment())
//            } else {
//                Utils.switchFragment(this, SplashFragment())
//            }
//        }
//        auth.addAuthStateListener(authStateListener)
//
//    }
//
//    override fun onLoginButtonPressed() {
//        val providers = arrayListOf(
//            AuthUI.IdpConfig.PhoneBuilder().build()
//            )
//
//        val loginIntent =  AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .build()
//
//        startActivityForResult(loginIntent, RC_SIGN_IN)
//    }


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
            R.id.action_settings ->{
//                auth.signOut()
//                auth.removeAuthStateListener(authStateListener)
//                Utils.switchFragment(this, SplashFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onSuspectSelected(suspect: Suspect) {
        Log.d(Constants.TAG, "selected: ${suspect.name}")
        Utils.switchFragment(this, MainInfoFragment.newInstance(suspect))
    }

    override fun onReportSelected(report: Report, suspect: Suspect) {
        Log.d(Constants.TAG, "selected: ${report.title} in ${suspect.name}")
        Utils.switchFragment(this, ReportDetailFragment.newInstance(report, suspect))
    }
}

//TODO: import address book
