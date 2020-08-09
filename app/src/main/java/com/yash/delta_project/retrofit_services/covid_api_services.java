package com.yash.delta_project.retrofit_services;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class covid_api_services {
    public static String BASE_URL_OF_INDIAN_STATES_DATA = "https://api.covid19india.org/";
    public static String BASE_URL_OF_ALL_COUNTRIES_DATA = "https://api.covid19api.com/";
    public static String BASE_URL_OF_INDIA_TOTAL_DATA = "https://coronavirus-19-api.herokuapp.com/countries/";

    public static Retrofit get_countries() {
        return new Builder().baseUrl(BASE_URL_OF_ALL_COUNTRIES_DATA).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static Retrofit get_total_india_data() {
        return new Builder().baseUrl(BASE_URL_OF_INDIA_TOTAL_DATA).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static Retrofit get_indian_states_data() {
        return new Builder().baseUrl(BASE_URL_OF_INDIAN_STATES_DATA).addConverterFactory(GsonConverterFactory.create()).build();
    }
}
