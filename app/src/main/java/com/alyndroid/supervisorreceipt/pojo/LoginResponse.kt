package com.alyndroid.supervisorreceipt.pojo


data class LoginResponse(
    val `data`: UserData,
    val error: String,
    val status: Boolean
)