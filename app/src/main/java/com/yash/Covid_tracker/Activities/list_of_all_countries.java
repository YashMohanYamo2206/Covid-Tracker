package com.yash.Covid_tracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.yash.Covid_tracker.Adapters.*;
import com.yash.Covid_tracker.R;
import com.yash.Covid_tracker.gson_converters.SortByName;
import com.yash.Covid_tracker.gson_converters.country;
import com.yash.Covid_tracker.gson_converters.list_of_countries;
import com.yash.Covid_tracker.interfaces.countries_interface;
import com.yash.Covid_tracker.retrofit_services.covid_api_services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class list_of_all_countries extends AppCompatActivity implements countries_list_adapter.OnItemClickListener {
    RecyclerView recyclerView;
    countries_list_adapter adapter;
    SearchView searchView;
    list_of_countries list_of_countries;
    TextView loading;
    ShimmerFrameLayout shimmerFrameLayout;
    List<country> countries = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);
        list_of_countries = new list_of_countries();
        loading = findViewById(R.id.loading);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_action(query);
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
        countries = new ArrayList<>();
        for (country country : list_of_countries.getCountries()) {
            if (country.getCountry().toLowerCase().contains(newText.toLowerCase())) {
                countries.add(country);
            }
        }
        adapter = new countries_list_adapter(countries);
        adapter.setOnItemClickListener(this);
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
            public void onResponse(@NonNull Call<List<country>> call,@NonNull Response<List<country>> response) {
                if (!response.isSuccessful()) {
                    loading.setText(R.string.network_error_message);
                    shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 1000);
                    return;
                }
                loading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                list_of_countries.setCountries(response.body());
                Collections.sort(list_of_countries.getCountries(), new SortByName());
                countries=list_of_countries.getCountries();
                adapter = new countries_list_adapter(countries);
                adapter.setOnItemClickListener(list_of_all_countries.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<com.yash.Covid_tracker.gson_converters.country>> call, @NonNull Throwable t) {
                loading.setText(R.string.network_error_message);
                shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 1000);
                Log.e("onFailure: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
        Log.d("onItemClick: ", String.valueOf(position));
        Intent intent = new Intent(this, countries_cases_details_activity.class);
        intent.putExtra("slug",countries.get(position).getSlug());
        intent.putExtra("countryName",countries.get(position).getCountry());
        intent.putExtra("imgUrl",countries.get(position).getISO2());
        startActivity(intent);
    }
}