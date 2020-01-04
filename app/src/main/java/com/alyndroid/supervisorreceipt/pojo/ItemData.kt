package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemData(
    val customername: String="",
    val customerno: String="",
    val id: Int=-1,
    val itemcategory: String="",
    val item_type: String="",
    val itemname: String="",
    val itemno: String="",
    val order: String="",
    var quantity: String="",
    var editedQuantity: String = quantity,
    val salesmanname: String="",
    var reason: String = "",
    var invoice_id: String = "",
    var status: Int = 0,
    val salesmanno: String=""
): Parcelable