package com.alyndroid.supervisorreceipt.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.helpers.handleError
import com.alyndroid.supervisorreceipt.pojo.LoginResponse
import com.alyndroid.supervisorreceipt.pojo.SettingsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse>
        get() = _response

    private val _settingsResponse = MutableLiveData<SettingsData>()
    val settingsResponse: LiveData<SettingsData>
        get() = _settingsResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _error = MutableLiveData<Int>()
    val error: LiveData<Int>
        get() = _error

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun login(code: String, password: String) {
        val map = HashMap<Any, Any>()
        map["phone"] = code
        map["password"] = password
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.loginAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _response.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }

    fun checkVersion() {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.getSettingsAsync()
            try {
                val stringResult = loginDeferred.await()
                _settingsResponse.value = stringResult.data[0]
                _loading.value = false
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }



}