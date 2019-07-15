package com.example.domesticviolencealert

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Suspect (var phone: String, var email: String, var name: String, var reports: ArrayList<Report>) : Parcelable

//TODO: image, score, color