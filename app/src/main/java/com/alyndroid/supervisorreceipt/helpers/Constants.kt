package com.alyndroid.supervisorreceipt.helpers

interface SharedPref{
    companion object {
        val app_version = "app_version"
        val gard_number = "gard_number"
        val min_needed_items = "min_needed_items"
    }
}

interface ErrorMsg{
    companion object {
        val no_connection = 1
        val internal_server = 2
    }
}