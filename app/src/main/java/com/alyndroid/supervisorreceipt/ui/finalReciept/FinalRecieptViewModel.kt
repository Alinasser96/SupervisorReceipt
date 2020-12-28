package com.alyndroid.supervisorreceipt.ui.finalReciept

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.helpers.handleError
import com.alyndroid.supervisorreceipt.pojo.*
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FinalRecieptViewModel : ViewModel() {
    private val _response = MutableLiveData<FinalRecieptData>()
    val response: LiveData<FinalRecieptData>
        get() = _response


    private val _coItemsResponse = MutableLiveData<CoordinatorItemsData>()
    val coItemsResponse: LiveData<CoordinatorItemsData>
        get() = _coItemsResponse


    private val _sendInvoiceResponse = MutableLiveData<SendInvoiceResponce>()
    val sendInvoiceResponse: LiveData<SendInvoiceResponce>
        get() = _sendInvoiceResponse

    private val _sendSalesmanInvoiceResponse = MutableLiveData<SendInvoiceResponce>()
    val sendSalesmanInvoiceResponse: LiveData<SendInvoiceResponce>
        get() = _sendSalesmanInvoiceResponse

    private val _families = MutableLiveData<List<String>>()
    val families: LiveData<List<String>>
        get() = _families

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _sendGardResponse = MutableLiveData<LoginResponse>()
    val sendGardResponse: LiveData<LoginResponse>
        get() = _sendGardResponse

    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean>
        get() = _empty

    private val _error = MutableLiveData<Int>()
    val error: LiveData<Int>
        get() = _error

    private val _fromSV = MutableLiveData<Boolean>()
    val fromSV: LiveData<Boolean>
        get() = _fromSV

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getAllItems(code: String) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.getAllItemsAsync(code)
            try {
                val stringResult = loginDeferred.await()
                _response.value = stringResult.data
                _loading.value = false
                _empty.value = stringResult.data.items.isEmpty()
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }


    fun getAllCoItems(customerCode: String) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred =
                    ApiInterface.SNBApi.retrofitService.getAllCoItemsAsync(customerCode)
            try {
                val stringResult = loginDeferred.await()
                _empty.value = stringResult.data.items.isEmpty()
                _coItemsResponse.value = stringResult.data
                _loading.value = false
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }
    fun getAllItems( salesManCode: String, customerCode: String) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred=
                    ApiInterface.SNBApi.retrofitService.getAllItemsAsync(salesManCode, customerCode)

            try {
                val stringResult = loginDeferred!!.await()
                _fromSV.value = (stringResult.data.type == "sv")
                _families.value = stringResult.data.items.map { it.itemcategory }.distinct()
                _empty.value = stringResult.data.items.isEmpty()
                _response.value = stringResult.data
                _loading.value = false
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }

    fun sendGard(map: HashMap<String, Any>) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        map["date"] = formatted
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.sendGardAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _sendGardResponse.value = stringResult
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }

    fun sendCoGard(requestBody: RequestBody) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.sendCoGardAsync(requestBody)
            try {
                val stringResult = loginDeferred.await()
                _sendGardResponse.value = stringResult
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }

    fun sendSupervisorInvoice(list: MutableList<ItemData>, supervisorNo: String) {
        for (i in list) {
            if (i.default_unit == i.large_unit) {
                i.editedQuantity = (i.editedQuantity.toDouble() * i.unit_factor!!).toString()
            }
        }
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        val map = HashMap<String, Any>()
        map["salesman_id"] = list[0].salesmanno
        map["supervisor_id"] = supervisorNo
        map["customer_id"] = list[0].customerno
        map["date"] = formatted
        map["item_no"] = list.map { d -> d.itemno }
        map["suggested_quantity"] = list.map { d -> d.quantity }
        map["supervisor_quantity"] = list.map { d -> d.editedQuantity }
        map["edit_reason"] = list.map { d -> d.reason }
        map["status"] = list.map { d -> d.status }
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.sendSupervisorInvoiceAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _sendInvoiceResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }

    fun sendSalesmanInvoice(list: MutableList<ItemData>, salesmanNo: String, customerNo: String) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        val map = HashMap<String, Any>()
        map["salesman_id"] = salesmanNo
        map["customer_id"] = customerNo
        map["date"] = formatted
        map["item_id"] = list.map { d -> d.itemno }
        map["quantity"] = list.map { d -> d.quantity }
        map["type"] = list.map { d -> d.item_type }
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.sendSalesmanInvoiceAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _sendSalesmanInvoiceResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _error.value = handleError(e)
                _loading.value = false
            }
        }
    }


}