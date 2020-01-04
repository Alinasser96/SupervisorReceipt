package com.alyndroid.supervisorreceipt.ui.subordinates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.pojo.SalesManData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SalesMenViewModel : ViewModel() {

    private val _response = MutableLiveData<List<SalesManData>>()
    val response: LiveData<List<SalesManData>>
        get() = _response


    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getAllSalesMen(code: String) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.getAllSalesMenAsync(code)
            try {
                val stringResult = loginDeferred.await()
                _response.value = stringResult.data
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}