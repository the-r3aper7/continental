package com.t27.continental.data.network


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.t27.continental.data.models.Locations
import com.t27.continental.data.models.Products
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private val BASE_URL = "https://t27-mashina.vercel.app/api/"

private val contentType = "application/json".toMediaType()
private val json = Json { ignoreUnknownKeys = true }

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory(contentType))
    .baseUrl(BASE_URL)
    .build()

interface MashinaApiService {
    @GET("blinkit")
    suspend fun searchBlinkitProducts(
        @Query("q") q: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("gr") gr: String
    ): Products

    @GET("instamart")
    suspend fun searchInstamartProducts(
        @Query("q") q: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("storeId") storeId: String
    ): Products

    @FormUrlEncoded
    @POST("similar")
    suspend fun similarProducts(
        @Field("source") source: String,
        @Field("targets") targets: String,
        @Field("product") product: String,
    ): Products

    @GET("{source}/change-location")
    suspend fun searchChangeLocation(
        @Path("source") source: String,
        @Query("q") q: String
    ): Locations
}

object MashinaApi {
    val retrofitService: MashinaApiService by lazy {
        retrofit.create(MashinaApiService::class.java)
    }
}