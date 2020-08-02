package com.yash.delta_project.gson_converters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.yash.delta_project.Models.list_of_all_countries;
import com.yash.delta_project.R;
import com.yash.delta_project.interfaces.india_total_data;
import com.yash.delta_project.retrofit_services.covid_api_services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SortbyName implements Comparator<country> {
    @Override
    public int compare(country o1, country o2) {
        return o1.getCountry().compareTo(o2.getCountry());
    }
}



