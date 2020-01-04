package com.alyndroid.supervisorreceipt.pojo

data class FinalRecieptResponse(
    val `data`: List<ItemData>,
    val error: String,
    val status: Boolean
)