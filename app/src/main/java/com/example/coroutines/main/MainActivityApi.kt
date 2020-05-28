package com.example.coroutines.main

import com.example.coroutines.main.data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MainActivityApi {

    @GET("breeds/list/all")
    fun getAllBreeds(): Call<ApiResponse<Map<String, List<String>>>>

    @GET("breeds/list/all")
    fun getBreedsListAsync(): Call<ApiResponse<Map<String, List<String>>>>

    @GET("breed/{breedName}/images/random")
    fun getImageByUrlAsync(@Path("breedName") breedName: String): Call<ApiResponse<String>>

    @GET("breed/{breedName}/images/random")
    suspend fun getImageByUrl(@Path("breedName") breedName: String): ApiResponse<String>

    @GET("breeds/list/all")
    suspend fun getBreedsList(): ApiResponse<Map<String, List<String>>>
}