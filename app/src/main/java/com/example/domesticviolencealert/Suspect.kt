package com.example.domesticviolencealert

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Suspect (var phone: String = "",
                    var email: String = "",
                    var name: String = "",
                    var proofsImages: ArrayList<String> = arrayListOf(),
                    var reports: ArrayList<Report> = arrayListOf(),
                    var score: Int = 50
) : Parcelable {
    @get: Exclude var id = ""
    @ServerTimestamp var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        fun fromSnapshot(snapshot: DocumentSnapshot): Suspect {
            val s = snapshot.toObject(Suspect::class.java)!!
            s.id = snapshot.id
            return s
        }
    }
}

