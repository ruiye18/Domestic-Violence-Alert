package com.example.domesticviolencealert

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Report (var title: String = "",
                   var description: String = "",
                   var isCriminal: Boolean = false
) : Parcelable

//TODO: images