package com.example.domesticviolencealert

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Suspect (var phone: String, var email: String, var name: String, var reports: ArrayList<Report>, var score: Int) : Parcelable

//TODO: image
//TODO: separate reports into crime record and proofs