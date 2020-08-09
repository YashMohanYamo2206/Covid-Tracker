package com.yash.delta_project.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.yash.delta_project.R;
import com.yash.delta_project.gson_converters.global;
import com.yash.delta_project.gson_converters.india_data;
import com.yash.delta_project.gson_converters.world_data;
import com.yash.delta_project.interfaces.countries_interface;
import com.yash.delta_project.interfaces.india_total_data;
import com.yash.delta_project.retrofit_services.covid_api_services;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    com.github.mikephil.charting.charts.PieChart PieChart;
    List<Integer> colors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise_total_world_retrofit_services();
        initialise_total_india_retrofit_services();
    }

    private void initialise_total_world_retrofit_services() {
        countries_interface india_total_data = covid_api_services.get_countries().create(countries_interface.class);
        india_total_data.getWorldData().enqueue(new Callback<world_data>() {
            @Override
            public void onResponse(@NonNull Call<world_data> call, @NonNull Response<world_data> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(com.yash.delta_project.Models.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                colors.add(Color.YELLOW);
                colors.add(Color.RED);
                colors.add(Color.BLUE);
                PieChart = findViewById(R.id.all_countries_pie_chart);
                ArrayList<PieEntry> entries = new ArrayList<>();
                assert response.body() != null;
                global global = response.body().getGlobal();
                entries.add(new PieEntry(global.getTotalRecovered(), "Recovered Cases"));
                entries.add(new PieEntry(global.getTotalDeaths(), "Deaths"));
                entries.add(new PieEntry(global.getTotalConfirmed() - global.getTotalDeaths() - global.getTotalRecovered(), "Active Cases"));
                PieDataSet data_set = new PieDataSet(entries, "");
                data_set.setValueTextSize(13f);
                data_set.setColors(colors);
                PieData pieData = new PieData(data_set);
                PieChart.setDrawEntryLabels(false);
                Description description = new Description();
                description.setText("WORLD DATA ");
                description.setTextColor(Color.YELLOW);
                description.setTextSize(13f);
                PieChart.setDescription(description);
                PieChart.setData(pieData);
                PieChart.animateY(1500);
                PieChart.invalidate();
            }

            @Override
            public void onFailure(@NonNull Call<world_data> call, @NonNull Throwable t) {
                Toast.makeText(com.yash.delta_project.Models.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialise_total_india_retrofit_services() {
        india_total_data india_total_data = covid_api_services.get_total_india_data().create(com.yash.delta_project.interfaces.india_total_data.class);
        india_total_data.getIndiaData().enqueue(new Callback<india_data>() {
            @Override
            public void onResponse(@NonNull Call<india_data> call, @NonNull Response<india_data> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(com.yash.delta_project.Models.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                colors.add(Color.YELLOW);
                colors.add(Color.RED);
                colors.add(Color.BLUE);
                PieChart = findViewById(R.id.india_pie_chart);
                ArrayList<PieEntry> entries = new ArrayList<>();
                assert response.body() != null;
                entries.add(new PieEntry(response.body().getRecovered(), "Recovered Cases"));
                entries.add(new PieEntry(response.body().getDeaths(), "Deaths"));
                entries.add(new PieEntry(response.body().getActive(), "Active Cases"));
                PieDataSet data_set = new PieDataSet(entries, "");
                data_set.setValueTextSize(13f);
                data_set.setColors(colors);
                PieData pieData = new PieData(data_set);
                PieChart.setDrawEntryLabels(false);
                Description description = new Description();
                description.setText("INDIA DATA ");
                description.setTextColor(Color.YELLOW);
                description.setTextSize(13f);
                PieChart.setDescription(description);
                PieChart.setData(pieData);
                PieChart.animateY(1500);
                PieChart.invalidate();
            }

            @Override
            public void onFailure(@NonNull Call<india_data> call, @NonNull Throwable t) {
                Toast.makeText(com.yash.delta_project.Models.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void all_countries(View view) {
        startActivity(new Intent(this, list_of_all_countries.class));
    }

    public void indian_states(View view) {
       startActivity(new Intent(this,list_of_all_indian_states.class));
    }
}
