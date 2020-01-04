package com.alyndroid.supervisorreceipt.pojo

data class FamiliesResponse(
    val `data`: List<FamiliesData>,
    val error: String,
    val status: Boolean
)