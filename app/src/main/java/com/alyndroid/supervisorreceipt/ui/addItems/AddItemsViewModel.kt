package com.alyndroid.supervisorreceipt.ui.addItems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.pojo.FamiliesResponse
import com.alyndroid.supervisorreceipt.pojo.ItemsByFamilyResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddItemsViewModel: ViewModel() {

    private val _response = MutableLiveData<FamiliesResponse>()
    val response: LiveData<FamiliesResponse>
        get() = _response

    private val _itemResponse = MutableLiveData<ItemsByFamilyResponse>()
    val itemResponse: LiveData<ItemsByFamilyResponse>
        get() = _itemResponse
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getFamilies() {
        coroutineScope.launch {
            _loading.value = true
            val familiesDeferred = ApiInterface.SNBApi.retrofitService.getAllFamiliesAsync()
            try {
                val stringResult = familiesDeferred.await()
                _response.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun getItemByFamiliy(familyCode : Int) {
        coroutineScope.launch {
            _loading.value = true
            val familiesDeferred = ApiInterface.SNBApi.retrofitService.getItemsByFamiliy(familyCode)
            try {
                val stringResult = familiesDeferred.await()
                _itemResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }
}
