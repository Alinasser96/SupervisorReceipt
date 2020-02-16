package com.alyndroid.supervisorreceipt.pojo

data class FinalRecieptData(
    val items: List<ItemData>,
    val new: List<ItemData>,
    val type: String
)