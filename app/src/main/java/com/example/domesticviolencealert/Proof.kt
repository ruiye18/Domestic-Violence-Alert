package com.example.domesticviolencealert

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Proof (var text: String = "",
                  var proofImage: String = ""
) : Parcelable