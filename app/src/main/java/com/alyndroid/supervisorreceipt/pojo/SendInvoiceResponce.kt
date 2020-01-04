package com.alyndroid.supervisorreceipt.pojo

data class SendInvoiceResponce(
    val `data`: SentInvoiceData,
    val error: String,
    val status: Boolean
)