package com.example.domesticviolencealert

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search.view.home_button

class SearchFragment : Fragment(){

    private lateinit var allSuspects: ArrayList<Suspect>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(Constants.TAG, "Enter search frag")

        allSuspects = Utils.loadSuspects()
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

        view.contacts_button.setOnClickListener {
            Utils.switchFragment(context!!, ContactsFragment.newInstance(allSuspects))
        }

        return view
    }


    private fun searchForResults(phone: String, email: String, name: String) {
        Log.d(Constants.TAG, "Searching results for $phone and $email and $name in ${allSuspects.size}")

        val suspects = ArrayList<Suspect>()

        suspects.addAll(searchByPhone(phone))
        allSuspects.removeAll(suspects)
        suspects.addAll(searchByEmail(email))
        allSuspects.removeAll(suspects)
        suspects.addAll(searchByName(name))

        if (phone.isEmpty() && email.isEmpty() && name.isEmpty()) {
            suspects.addAll(allSuspects)
        }

        Utils.switchFragment(context!!, SuspectListFragment.newInstance(suspects))
    }

    private fun searchByPhone(phone: String): ArrayList<Suspect> {
        val toReturn = ArrayList<Suspect>()
        if (allSuspects.isNotEmpty()) {
            for (s in allSuspects) {
                if (s.phone == phone) {
                    toReturn.add(s)
                }
            }
        }
        return toReturn
    }

    private fun searchByEmail(email: String): ArrayList<Suspect> {
        val toReturn = ArrayList<Suspect>()
        if (allSuspects.isNotEmpty()) {
            for (s in allSuspects) {
                if (s.email == email) {
                    toReturn.add(s)
                }
            }
        }
        return toReturn
    }

    private fun searchByName(name: String): ArrayList<Suspect> {
        val toReturn = ArrayList<Suspect>()
        if (allSuspects.isNotEmpty()) {
            for (s in allSuspects) {
                if (s.name.contains(name) && name.isNotEmpty()) {
                    toReturn.add(s)
                }
            }
        }
        return toReturn
    }

}