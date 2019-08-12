package com.example.domesticviolencealert

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime


@Parcelize
data class Report (var title: String = "",
                   var info: String = "",
                   var isCriminal: Boolean = false,
                   var reportImage: String = "",
                   var agree: Boolean = true,
                   var addDate: String = ""
) : Parcelable
