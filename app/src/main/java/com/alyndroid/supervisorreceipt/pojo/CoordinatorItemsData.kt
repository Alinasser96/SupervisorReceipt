package com.alyndroid.supervisorreceipt.pojo

data class CoordinatorItemsData(
    val items: List<CoordinatorItemData>,
    val new: List<CoordinatorItemData>,
    val type: String
)