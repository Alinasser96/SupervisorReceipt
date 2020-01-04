package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GardModel(val itemName: String, var itemCount: Int = 0) : Parcelable