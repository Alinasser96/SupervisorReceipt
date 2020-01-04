package com.alyndroid.supervisorreceipt.pojo

data class ItemsByFamilyResponse(
    val `data`: List<ItemByFamiliyData>,
    val error: String,
    val status: Boolean
)