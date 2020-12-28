package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class All(
    val categoryno: String,
    val customernamea: String,
    val customerno: String,
    val description: String?,
    val latitude: String,
    val longitude: String,
    val routeID: String?,
    val routenamea: String?,
    val salesmannamea: String?,
    val salesmanno: String?,
    var type: Int
): Parcelable