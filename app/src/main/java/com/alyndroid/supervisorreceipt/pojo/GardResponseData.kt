package com.alyndroid.supervisorreceipt.pojo

data class GardResponseData(
    val customer_id: String,
    val date: String,
    val id: Int,
    val invoice_id: Any,
    val salesman_id: String,
    val supervisor_invoice: Any
)