package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomersResponse(
    val `data`: List<CustomersData>,
    val error: String,
    val status: Boolean
): Parcelable