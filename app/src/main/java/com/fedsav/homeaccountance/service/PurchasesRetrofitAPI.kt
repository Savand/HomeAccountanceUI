package com.fedsav.homeaccountance.service

import com.fedsav.homeaccountance.dto.PurchaseItemDto
import retrofit2.Call
import retrofit2.http.*

interface PurchasesRetrofitAPI {

    @POST("purchases")
    fun postPurchaseItem(@Body purchaseItemDto: PurchaseItemDto?): Call<Map<String, String>?>?

    @GET("purchases")
    fun getPurchaseItemList(): Call<List<PurchaseItemDto>?>?

    @DELETE("purchases/{id}")
    fun removePurchaseItem(@Path("id") id: String): Call<Void>

}