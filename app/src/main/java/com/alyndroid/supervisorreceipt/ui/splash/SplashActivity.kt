package com.alyndroid.supervisorreceipt.ui.splash

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.BuildConfig
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.helpers.SharedPref
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.helpers.buildWifiDialog
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import com.alyndroid.supervisorreceipt.ui.login.LoginActivity
import com.alyndroid.supervisorreceipt.ui.login.LoginViewModel
import com.alyndroid.supervisorreceipt.ui.subordinates.SalesMenActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    private var versionCode = BuildConfig.VERSION_CODE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        viewModel.response.observe(this, Observer {
            if (it.status) {
                if (it.data.type == "sm" || it.data.type == "co") {
                    val intent = Intent(this, FiltersActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (it.data.type == "sv" ) {
                    val intent = Intent(this, SalesMenActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewModel.error.observe(this, Observer {
            when(it){
                1-> buildWifiDialog(this)
            }
        })

        viewModel.settingsResponse.observe(this, Observer {
            SharedPreference(this).save(SharedPref.app_version, it.app_version)
            SharedPreference(this).save(SharedPref.gard_number, it.gard_number)
            SharedPreference(this).save(SharedPref.min_needed_items, it.min_needed_items.toString())

            if (it.app_version.toInt() > versionCode) {
                showUpdateVersionDialog()
            } else {
                login()
            }
        })
    }


    private fun showUpdateVersionDialog() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.update_version)
            .setMessage(R.string.update_version_available)
            .setPositiveButton(R.string.update) { _: DialogInterface, _: Int ->
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id$appPackageName")
                        )
                    )
                } catch (activityNotFoundException: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }.create().show()
    }

    private fun login() {
        if (SharedPreference(this).getValueString("phone") != null) {
            viewModel.login(
                SharedPreference(this).getValueString("phone")!!,
                SharedPreference(this).getValueString("password")!!
            )
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkVersion()
    }
}
