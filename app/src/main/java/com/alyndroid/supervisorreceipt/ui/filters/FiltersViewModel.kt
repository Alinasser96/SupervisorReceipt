package com.alyndroid.supervisorreceipt.ui.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.pojo.CustomersData
import com.alyndroid.supervisorreceipt.pojo.CustomersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FiltersViewModel: ViewModel() {
    private val _response = MutableLiveData<List<CustomersData>>()
    val response: LiveData<List<CustomersData>>
        get() = _response

    private val _allResponse = MutableLiveData<CustomersResponse>()
    val allResponse: LiveData<CustomersResponse>
        get() = _allResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getAllCustomers(code: String) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.getAllCustomersAsync(code)
            try {
                val stringResult = loginDeferred.await()
                _allResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun getAllCoCustomers(code: Int) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.getAllCoCustomersAsync(code)
            try {
                val stringResult = loginDeferred.await()
                _allResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}