package com.example.domesticviolencealert

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.firestore.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit


object Utils {

    private val suspectsRef = FirebaseFirestore
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
                } else {
                    for (docChange in snapshot!!.documentChanges) {
                        val suspect = Suspect.fromSnapshot(docChange.document)
                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                suspects.add(0, suspect)
                            }
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

    fun compareDates(date: LocalDate) : Int {
        val current = LocalDate.now()
        val passDates = ChronoUnit.DAYS.between(date, current)
        return passDates.toInt()
    }


    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        return currentDate.format(formatter).toString()
    }

    fun getPassDatesString(passDates : Int, context: Context) : String {
        when {
            passDates == 0 -> return context.getString(R.string.today)
            passDates < 7 ->  return context.getString(R.string.count_dates, passDates.toString())
            passDates == 7 -> return context.getString(R.string.one_week_ago)
        }
        return ""
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