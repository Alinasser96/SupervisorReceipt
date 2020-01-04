package com.alyndroid.supervisorreceipt.ui.finalReciept

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alyndroid.supervisorreceipt.data.ApiInterface
import com.alyndroid.supervisorreceipt.pojo.ItemData
import com.alyndroid.supervisorreceipt.pojo.SendInvoiceResponce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FinalRecieptViewModel : ViewModel() {
    private val _response = MutableLiveData<List<ItemData>>()
    val response: LiveData<List<ItemData>>
        get() = _response

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

    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean>
        get() = _empty

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
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun getAllItems(salesManCode: String, customerCode: String) {
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred =
                ApiInterface.SNBApi.retrofitService.getAllItemsAsync(salesManCode, customerCode)
            try {
                val stringResult = loginDeferred.await()
                _response.value = stringResult.data
                _families.value = stringResult.data.map { it.itemcategory }.distinct()
                _empty.value = stringResult.data.isEmpty()
                _loading.value = false
            } catch (e: Exception) {
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
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

    fun sendSupervisorInvoice(list: MutableList<ItemData>, supervisorNo : String) {
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
        map["type"] = list.map { d -> d.item_type}
        coroutineScope.launch {
            _loading.value = true
            val loginDeferred = ApiInterface.SNBApi.retrofitService.sendSalesmanInvoiceAsync(map)
            try {
                val stringResult = loginDeferred.await()
                _sendSalesmanInvoiceResponse.value = stringResult
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
            }
        }
    }

}