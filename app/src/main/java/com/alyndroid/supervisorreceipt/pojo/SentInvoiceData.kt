package com.alyndroid.supervisorreceipt.pojo

data class SentInvoiceData(
    val created_at: String,
    val customer_id: String,
    val date: String,
    val id: Int,
    val salesman_id: String,
    val supervisor_id: String,
    val updated_at: String
)