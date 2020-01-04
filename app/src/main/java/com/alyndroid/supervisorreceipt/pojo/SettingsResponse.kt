package com.alyndroid.supervisorreceipt.pojo

data class SettingsResponse(
    val `data`: List<SettingsData>,
    val error: String,
    val status: Boolean
)