package com.yash.delta_project.Models;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.yash.delta_project.Adapters.*;
import com.yash.delta_project.R;
import com.yash.delta_project.gson_converters.SortbyName;
import com.yash.delta_project.gson_converters.country;
import com.yash.delta_project.gson_converters.list_of_countries;
import com.yash.delta_project.interfaces.countries_interface;
import com.yash.delta_project.retrofit_services.covid_api_services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class list_of_all_countries extends AppCompatActivity {
    RecyclerView recyclerView;
    countries_list_adapter adapter;
    SearchView searchView;
    list_of_countries list_of_countries;
    TextView loading;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);
        loading = findViewById(R.id.loading);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_action(newText);
                return false;
            }
        });
        init_recyclerview();
        init();
    }

    private void search_action(String newText) {
        List<country> countries = new ArrayList<>();
        for (country country : list_of_countries.getCountries()) {
            if (country.getCountry().toLowerCase().contains(newText.toLowerCase())) {
                countries.add(country);
            }
        }
        adapter = new countries_list_adapter(countries);
        recyclerView.setAdapter(adapter);
    }

    private void init_recyclerview() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void init() {
        countries_interface covid19Interface = covid_api_services.get_countries().create(countries_interface.class);
        covid19Interface.getCountries().enqueue(new Callback<List<country>>() {
            @Override
            public void onResponse(Call<List<country>> call, Response<List<country>> response) {
                if (!response.isSuccessful()) {
                    loading.setText(R.string.network_error_message);
                    shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 1000);
                    return;
                }
                loading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                list_of_countries = new list_of_countries();
                list_of_countries.setCountries(response.body());
                Collections.sort(list_of_countries.getCountries(), new SortbyName());
                adapter = new countries_list_adapter(list_of_countries.getCountries());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<com.yash.delta_project.gson_converters.country>> call, Throwable t) {
                loading.setText(R.string.network_error_message);
                shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 1000);
                Log.e("onFailure: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}