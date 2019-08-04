package com.example.domesticviolencealert

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.*


object Utils {
    val suspects = ArrayList<Suspect>()

    val suspectsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.SUSPECTS_COLLECTION)

    init {
      loadSuspects()
    }

    fun loadSuspects(): ArrayList<Suspect> {
        val suspects = ArrayList<Suspect>()
        suspectsRef
            .orderBy(Suspect.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e(Constants.TAG, "listen error: $exception")
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges) {
                    val suspect = Suspect.fromSnapshot(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            suspects.add(0, suspect)
                            Log.d(Constants.TAG, "Added suspect success with size ${suspects.size}")
                        }
                    }
                }
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

    fun addSuspect(suspect: Suspect) {
        suspectsRef.add(suspect)
    }

    fun addReport(list: ArrayList<Suspect>, current: Suspect, report: Report) {
        val pos = list.indexOfFirst { current.id == it.id }
        Log.d(Constants.TAG, "adding report to position ${pos} and id ${current.id} in ${list.size}")
    }

//    private fun localInit() {
//        val phones = arrayOf(
//            "0000000000",
//            "1111111111",
//            "2222222222"
//        )
//
//        val emails = arrayOf(
//            "example@email.com",
//            "exampleTest@email.com",
//            "exampleTestTest@email.com"
//        )
//
//        val names = arrayOf(
//            "Suspect 1",
//            "Suspect 2",
//            "Suspect 3"
//        )
//
//        val report1 = ArrayList<Report>()
//        report1.add(Report("Suspect 1 -> Report 1"))
//        report1.add(Report("Suspect 1 -> Report 2"))
//        report1.add(Report("Suspect 1 -> Report 3"))
//
//        val report2 = ArrayList<Report>()
//        report2.add(Report("Suspect 2 -> Report 1"))
//        report2.add(Report("Suspect 2 -> Report 2"))
//        report2.add(Report("Suspect 2 -> Report 3"))
//
//        val report3 = ArrayList<Report>()
//        report3.add(Report("Suspect 3 -> Report 1"))
//        report3.add(Report("Suspect 3 -> Report 2"))
//        report3.add(Report("Suspect 3 -> Report 3"))
//
//        val reports = arrayOf (
//            report1,
//            report2,
//            report3
//        )
//
//        val proofImages = ArrayList<String>()
//        proofImages.add("https://thefga.org/wp-content/uploads/2017/10/cut-red-tape.jpg")
//        proofImages.add("https://thefga.org/wp-content/uploads/2017/10/cut-red-tape.jpg")
//        proofImages.add("https://thefga.org/wp-content/uploads/2017/10/cut-red-tape.jpg")
//
//        val localSuspects = ArrayList<Suspect>()
//        for (i in phones.indices) {
//            val suspect = Suspect(phones[i], emails[i], names[i], proofImages, reports[i],25)
//            localSuspects.add(suspect)
//        }
//    }
}