package com.example.domesticviolencealert

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Report (var title: String = "",
                   var info: String = "",
                   var isCriminal: Boolean = false,
                   var reportImage: String = "",
                   var agree: Boolean = true
) : Parcelable

//TODO: Calender 