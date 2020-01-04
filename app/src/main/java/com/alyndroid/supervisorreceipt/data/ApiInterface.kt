package com.alyndroid.supervisorreceipt.data

import com.alyndroid.supervisorreceipt.pojo.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl("http://prohussein.com/snb/public/api/")
    .build()

interface ApiInterface {
    @POST("auth/login")
    fun loginAsync(@Body map: HashMap<String, String>): Deferred<LoginResponse>

    @POST("gard-items")
    fun sendGardAsync(@Body map: HashMap<String, Any>): Deferred<LoginResponse>

    @POST("supervisor/invoice")
    fun sendSupervisorInvoiceAsync(@Body map: HashMap<String, Any>): Deferred<SendInvoiceResponce>

    @POST("salesman/invoice")
    fun sendSalesmanInvoiceAsync(@Body map: HashMap<String, Any>): Deferred<SendInvoiceResponce>

    @GET("load/customers/{userCode}")
    fun getAllCustomersAsync(@Path("userCode") userCode: String): Deferred<CustomersResponse>

    @GET("load/suggested/items/{customerNo}")
    fun getAllItemsAsync(@Path("customerNo") customerNo: String): Deferred<FinalRecieptResponse>

    @GET("salesman-invoice/salesman-id/{salesManNo}/customer-id/{customerNo}")
    fun getAllItemsAsync(@Path("salesManNo") salesManNo: String, @Path("customerNo") customerNo: String): Deferred<FinalRecieptResponse>


    @GET("all-brands")
    fun getAllFamiliesAsync(): Deferred<FamiliesResponse>

    @GET("settings")
    fun getSettingsAsync(): Deferred<SettingsResponse>

    @GET("brand/items/{itemCode}")
    fun getItemsByFamiliy(@Path("itemCode") itemCode: Int): Deferred<ItemsByFamilyResponse>

    @GET("subordinates/{salesManNo}")
    fun getAllSalesMenAsync(@Path("salesManNo") customerNo: String): Deferred<SalesMenResponse>

    object SNBApi{
        val retrofitService: ApiInterface by lazy {
            retrofit.create(ApiInterface::class.java)
        }
    }
}

