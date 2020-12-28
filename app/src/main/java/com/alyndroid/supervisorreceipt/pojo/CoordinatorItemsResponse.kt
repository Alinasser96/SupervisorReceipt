package com.alyndroid.supervisorreceipt.pojo

data class CoordinatorItemsResponse(
    val `data`: CoordinatorItemsData,
    val error: String,
    val status: Boolean
)