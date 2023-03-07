package com.fedsav.homeaccountance.service

import com.fedsav.homeaccountance.dto.SendPurchaseItemRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchasesRetrofitAPI {

    @POST("purchases")
    // on below line we are creating a method to post our data.
    fun postData(@Body dataModal: SendPurchaseItemRequest?): Call<Map<String, String>?>?

}