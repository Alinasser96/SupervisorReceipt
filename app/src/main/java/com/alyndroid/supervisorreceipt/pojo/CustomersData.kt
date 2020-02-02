package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomersData(
    val all: List<All>,
    val new: List<New>,
    val new_item: List<NewItem>
):Parcelable