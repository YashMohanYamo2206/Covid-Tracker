package com.yash.delta_project.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yash.delta_project.R;
import com.yash.delta_project.gson_converters.countries_details;
import com.yash.delta_project.interfaces.countries_interface;
import com.yash.delta_project.retrofit_services.covid_api_services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class countries_cases_details_activity extends AppCompatActivity {
    List<Integer> deaths = new ArrayList<>();
    List<Integer> recovered = new ArrayList<>();
    List<Integer> active = new ArrayList<>();
    List<Integer> total_cases = new ArrayList<>();
    List<String> date = new ArrayList<>();
    LineChart daily_confirmed_cases, active_cases, recovered_cases, death_cases;
    TextView tv_countryName, tv_totalCases, tv_active, tv_recovered, tv_deaths, tv_lastUpdated;
    CoordinatorLayout coordinatorLayout;
    BottomSheetBehavior bottomSheetBehavior;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries_cases_details_activity);
        initialise_views();
        Intent intent = getIntent();
        initialise_retrofit_services(intent.getStringExtra("countryName"));
    }

    private void initialise_views() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        shimmerFrameLayout = findViewById(R.id.shimmer_country);
        tv_countryName = findViewById(R.id.country_name);
        tv_totalCases = findViewById(R.id.total_cases);
        tv_totalCases.getLayoutParams().width = coordinatorLayout.getWidth() / 2 - 10;
        tv_active = findViewById(R.id.active);
        tv_active.getLayoutParams().height = (coordinatorLayout.getWidth() / 2 - 10);
        tv_deaths = findViewById(R.id.deaths);
        tv_deaths.getLayoutParams().height = (coordinatorLayout.getWidth() / 2 - 10);
        tv_recovered = findViewById(R.id.recovered);
        tv_recovered.getLayoutParams().height = (coordinatorLayout.getWidth() / 2 - 10);
        tv_lastUpdated = findViewById(R.id.last_updated);

        View bottom_sheet = findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        // bottom_sheet.getLayoutParams().height = (int)tv_countryName.getY() + 20;


        daily_confirmed_cases = findViewById(R.id.daily_new_cases_line_chart);
        active_cases = findViewById(R.id.active_cases_line_chart);
        recovered_cases = findViewById(R.id.recovered_cases_line_chart);
        death_cases = findViewById(R.id.death_cases_line_chart);
    }


    private void initialise_retrofit_services(String s) {
        countries_interface api_services = covid_api_services.get_countries().create(countries_interface.class);
        api_services.getCountryDetail(s).enqueue(new Callback<List<countries_details>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<countries_details>> call, @NonNull Response<List<countries_details>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(countries_cases_details_activity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                assert response.body() != null;
                if (response.body().isEmpty()) {
                    tv_totalCases.setText(tv_totalCases.getText() + "NULL");
                    tv_active.setText(tv_active.getText() + "NULL");
                    tv_deaths.setText(tv_deaths.getText() + "NULL");
                    tv_recovered.setText(tv_recovered.getText() + "NULL");
                    return;
                }
                shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 500);
                tv_countryName.setText(response.body().get(0).getCountry().toUpperCase());
                for (countries_details countries_details : response.body()) {
                    deaths.add(countries_details.getDeaths());
                    recovered.add(countries_details.getRecovered());
                    total_cases.add(countries_details.getConfirmed());
                    active.add(countries_details.getConfirmed() - countries_details.getDeaths() - countries_details.getRecovered());
                    date.add(countries_details.getDate().substring(0, 10));
                }
                tv_totalCases.setText(tv_totalCases.getText() + String.valueOf(total_cases.get(total_cases.size() - 1)));
                tv_active.setText(tv_active.getText() + String.valueOf(active.get(active.size() - 1)));
                tv_deaths.setText(tv_deaths.getText() + String.valueOf(deaths.get(deaths.size() - 1)));
                tv_recovered.setText(tv_recovered.getText() + String.valueOf(recovered.get(recovered.size() - 1)));
                tv_lastUpdated.setText(tv_lastUpdated.getText() + " " + date.get(date.size() - 1));
                display_results_in_graphs(deaths, recovered, active, total_cases, date);
            }

            @Override
            public void onFailure(@NonNull Call<List<countries_details>> call, @NonNull Throwable t) {
                Log.d("onFailure: ", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(countries_cases_details_activity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void display_results_in_graphs(List<Integer> deaths,
                                           List<Integer> recovered,
                                           List<Integer> active,
                                           List<Integer> total_cases,
                                           List<String> date) {

        

    }


    public void back_button(View view) {
        finish();
    }
}