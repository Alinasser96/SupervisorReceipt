package com.alyndroid.supervisorreceipt.ui.gard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.pojo.GardRequest
import com.alyndroid.supervisorreceipt.pojo.LoginResponse
import com.alyndroid.supervisorreceipt.pojo.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GardViewModel: ViewModel(){
    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse>
        get() = _response
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


}