package com.alyndroid.supervisorreceipt.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alyndroid.supervisorreceipt.R
import com.alyndroid.supervisorreceipt.databinding.ActivityLoginBinding
import com.alyndroid.supervisorreceipt.helpers.SharedPreference
import com.alyndroid.supervisorreceipt.pojo.SalesMenResponse
import com.alyndroid.supervisorreceipt.ui.filters.FiltersActivity
import com.alyndroid.supervisorreceipt.ui.map.MapActivity
import com.alyndroid.supervisorreceipt.ui.subordinates.SalesMenActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.loginButton.attachTextChangeAnimator()
        binding.loginButton.setOnClickListener {
            viewModel.login(
                binding.codeEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }
        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.loginButton.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
                Toast.makeText(this, "loading", Toast.LENGTH_SHORT).show()
            } else {

            }
        })


        viewModel.response.observe(this, Observer {
            if (it.status) {

                SharedPreference(this).save("name", it.data.name)
                SharedPreference(this).save("password", binding.passwordEditText.text.toString())
                SharedPreference(this).save("user_id", it.data.id)
                SharedPreference(this).save("salesman_no", it.data.SalesmanNo)
                SharedPreference(this).save("type", it.data.type)

                if (it.data.type == "sm"){
                    val intent = Intent(this, FiltersActivity::class.java)
                    intent.putExtra("name", SharedPreference(this).getValueString("name"))
                    intent.putExtra("user_id", it.data.id.toString())
                    startActivity(intent)
                    finish()
                } else if (it.data.type == "sv") {
                    val intent = Intent(this,SalesMenActivity::class.java)
                    intent.putExtra("name", SharedPreference(this).getValueString("name"))
                    intent.putExtra("user_id", it.data.id.toString())
                    startActivity(intent)
                    finish()
                }

            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                binding.loginButton.hideProgress(R.string.login)
            }
        })
        binding.executePendingBindings()

    }
}
