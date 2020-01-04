package com.alyndroid.supervisorreceipt.pojo

data class SalesMenResponse(
    val `data`: List<SalesManData>,
    val error: String,
    val status: Boolean
)