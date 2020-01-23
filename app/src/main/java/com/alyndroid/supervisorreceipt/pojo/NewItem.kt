package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewItem(
    val customerno: String,
    val itemno: Int
): Parcelable