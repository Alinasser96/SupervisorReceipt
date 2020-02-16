package com.alyndroid.supervisorreceipt.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.ui.login.LoginActivity
import com.shreyaspatil.MaterialDialog.MaterialDialog
import retrofit2.HttpException
import java.net.UnknownHostException

fun handleError(e: Exception): Int {
    return when (e) {
        is UnknownHostException -> ErrorMsg.no_connection
        is HttpException -> ErrorMsg.internal_server
        else -> -1
    }
}

fun buildWifiDialog(context: Activity) {
    val builder = MaterialDialog.Builder(context)
    builder.setTitle(context.getString(R.string.no_internet_title))
        .setMessage(context.getString(R.string.no_internet_msg))
        .setPositiveButton(context.getString(R.string.open_wifi_now)) { dialog, _ ->
            Toast.makeText(context, context.getString(R.string.connect_to_wifi), Toast.LENGTH_LONG)
                .show()
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            dialog.cancel()
            context.startActivity(intent)
        }
        .setNegativeButton(context.getString(R.string.not_now)) { dialog, _ ->
            Toast.makeText(context, context.getString(R.string.disagree_msg), Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()
        }
        .build()
        .show()
}

public fun logoutAction(context: Activity) {
    val alertDialogBuilder = MaterialDialog.Builder(context)
    alertDialogBuilder.setMessage("هل أنت متأكد من تسجيل الخروج ؟")
        .setCancelable(false)
        .setPositiveButton(
            "نعم"
        ) { dialog, id ->
            SharedPreference(context).logout()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            dialog.cancel()
            context.startActivity(intent)
        }
        .setNegativeButton(
            "لا"
        ) { dialog, id -> dialog.cancel() }
        .build()
        .show()
}