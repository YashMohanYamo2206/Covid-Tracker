package com.yash.Covid_tracker.interfaces;

import com.yash.Covid_tracker.gson_converters.dummy_states_daily;
import com.yash.Covid_tracker.gson_converters.india_data;


import retrofit2.Call;
import retrofit2.http.GET;

public interface india_total_data {
    @GET("india")
    Call<india_data> getIndiaData();
    @GET("states_daily.json")
    Call<dummy_states_daily> getIndianStatesData();
}
