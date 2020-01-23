package com.alyndroid.supervisorreceipt.pojo

data class FinalRecieptResponse(
    val `data`: FinalRecieptData,
    val error: String,
    val status: Boolean
)