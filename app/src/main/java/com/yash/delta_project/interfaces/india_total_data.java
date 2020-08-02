package com.yash.delta_project.interfaces;

import com.yash.delta_project.gson_converters.india_data;

import retrofit2.Call;
import retrofit2.http.GET;

public interface india_total_data {
    @GET("india")
    Call<india_data> getIndiaData();
}
