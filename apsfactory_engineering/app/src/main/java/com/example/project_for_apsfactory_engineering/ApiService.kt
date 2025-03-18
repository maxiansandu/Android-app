package com.example.project_for_apsfactory_engineering

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query





interface ApiService {
    @GET("search")
    fun searchObjects(@Query("q") query: String): Call<SearchResponse>

    @GET("objects/{objectID}")
    fun getObjectDetails(@Path("objectID") objectId: Int): Call<ObjectResponse>
}





