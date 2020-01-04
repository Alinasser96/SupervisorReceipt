package com.alyndroid.supervisorreceipt.pojo

import android.os.Parcel
import android.os.Parcelable

data class CustomersData(
    val categoryno: String?,
    val customernamea: String?,
    val customerno: String?,
    val description: String?,
    val latitude: String?,
    val longitude: String?,
    val routeID: String?,
    val routenamea: String?,
    val salesmannamea: String?,
    val salesmanno: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryno)
        parcel.writeString(customernamea)
        parcel.writeString(customerno)
        parcel.writeString(description)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(routeID)
        parcel.writeString(routenamea)
        parcel.writeString(salesmannamea)
        parcel.writeString(salesmanno)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomersData> {
        override fun createFromParcel(parcel: Parcel): CustomersData {
            return CustomersData(parcel)
        }

        override fun newArray(size: Int): Array<CustomersData?> {
            return arrayOfNulls(size)
        }
    }
}