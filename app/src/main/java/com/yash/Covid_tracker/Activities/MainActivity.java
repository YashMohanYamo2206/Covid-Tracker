package com.yash.Covid_tracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.yash.Covid_tracker.Adapters.pinned_countries_list_adapter;
import com.yash.Covid_tracker.Adapters.pinned_indian_states_list_adapter;
import com.yash.Covid_tracker.DataBase.pinned_countries_DataBase;
import com.yash.Covid_tracker.DataBase.pinned_countries_contract;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_DataBase;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_contract;
import com.yash.Covid_tracker.R;
import com.yash.Covid_tracker.gson_converters.global;
import com.yash.Covid_tracker.gson_converters.india_data;
import com.yash.Covid_tracker.gson_converters.world_data;
import com.yash.Covid_tracker.interfaces.countries_interface;
import com.yash.Covid_tracker.interfaces.india_total_data;
import com.yash.Covid_tracker.retrofit_services.covid_api_services;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements pinned_countries_list_adapter.OnItemClickListener, pinned_indian_states_list_adapter.OnItemClickListener {
    com.github.mikephil.charting.charts.PieChart PieChart;
    List<Integer> colors = new ArrayList<>();
    RecyclerView recyclerView,recyclerView2;
    pinned_countries_list_adapter adapter;
    SQLiteDatabase mDatabase,mDatabaseStates;
    pinned_indian_states_list_adapter states_list_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_recyclerView();
        initialise_total_world_retrofit_services();
        initialise_total_india_retrofit_services();
    }

    private void init_recyclerView() {
        recyclerView2 = findViewById(R.id.pinned_states_recyclerView);
        recyclerView = findViewById(R.id.pinned_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        pinned_countries_DataBase dataBase1 = new pinned_countries_DataBase(this);
        mDatabase = dataBase1.getReadableDatabase();

        pinned_indian_states_DataBase database2 = new pinned_indian_states_DataBase(this);
        mDatabaseStates = database2.getReadableDatabase();

        adapter = new pinned_countries_list_adapter(getAllItems());
        states_list_adapter = new pinned_indian_states_list_adapter(getAllIndianStatesItems());

        adapter.setOnItemClickListener(this);
        states_list_adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(states_list_adapter);
    }



    private void initialise_total_world_retrofit_services() {
        countries_interface india_total_data = covid_api_services.get_countries().create(countries_interface.class);
        india_total_data.getWorldData().enqueue(new Callback<world_data>() {
            @Override
            public void onResponse(@NonNull Call<world_data> call, @NonNull Response<world_data> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(com.yash.Covid_tracker.Activities.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
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
                PieChart.setDrawEntryLabels(true);
                PieChart.setEntryLabelColor(Color.BLACK);
                Description description = new Description();
                description.setText("WORLD DATA ");
                description.setTextColor(Color.YELLOW);
                description.setTextSize(13f);
                PieChart.setDescription(description);
                PieChart.setHoleRadius(10f);
                PieChart.setData(pieData);
                PieChart.animateY(1000);
                PieChart.invalidate();
            }

            @Override
            public void onFailure(@NonNull Call<world_data> call, @NonNull Throwable t) {
                Toast.makeText(com.yash.Covid_tracker.Activities.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialise_total_india_retrofit_services() {
        india_total_data india_total_data = covid_api_services.get_total_india_data().create(com.yash.Covid_tracker.interfaces.india_total_data.class);
        india_total_data.getIndiaData().enqueue(new Callback<india_data>() {
            @Override
            public void onResponse(@NonNull Call<india_data> call, @NonNull Response<india_data> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(com.yash.Covid_tracker.Activities.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
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
                PieChart.setDrawEntryLabels(true);
                PieChart.setEntryLabelColor(Color.BLACK);
                Description description = new Description();
                description.setText("INDIA DATA ");
                description.setTextColor(Color.YELLOW);
                description.setTextSize(13f);
                PieChart.setDescription(description);
                PieChart.setHoleRadius(10f);
                PieChart.setData(pieData);
                PieChart.animateY(1000);
                PieChart.invalidate();
            }

            @Override
            public void onFailure(@NonNull Call<india_data> call, @NonNull Throwable t) {
                Toast.makeText(com.yash.Covid_tracker.Activities.MainActivity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Cursor getAllItems() {
        return mDatabase.query(
                pinned_countries_contract.pinned_countries_entry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                pinned_countries_contract.pinned_countries_entry.COLUMN_TIMESTAMP + " DESC"
        );
    }

    @Override
    protected void onPostResume() {
        init_recyclerView();
        initialise_total_india_retrofit_services();
        initialise_total_world_retrofit_services();
        super.onPostResume();
    }

    public void all_countries(View view) {
        startActivity(new Intent(this, list_of_all_countries.class));
    }

    public void indian_states(View view) {
        startActivity(new Intent(this, list_of_all_indian_states.class));
    }

    private Cursor getAllIndianStatesItems() {
        return mDatabaseStates.query(
                pinned_indian_states_contract.pinned_indian_states_entry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_TIMESTAMP + " DESC"
        );
    }
    @Override
    public void onItemCLick(int position) {
        Cursor cursor = getAllItems();
        cursor.moveToPosition(position);
        Log.d("onItemClick: ", String.valueOf(position));
        Intent intent = new Intent(this, countries_cases_details_activity.class);
        intent.putExtra("slug", cursor.getString(cursor.getColumnIndex(pinned_countries_contract.pinned_countries_entry.COLUMN_SLUG)));
        intent.putExtra("countryName", cursor.getString(cursor.getColumnIndex(pinned_countries_contract.pinned_countries_entry.COLUMN_COUNTRY_NAME)));
        intent.putExtra("imgUrl", cursor.getString(cursor.getColumnIndex(pinned_countries_contract.pinned_countries_entry.COLUMN_IMAGE_URL)));
        startActivity(intent);
    }

    @Override
    public void onStatesItemCLick(int position) {
        Cursor cursor = getAllIndianStatesItems();
        cursor.moveToPosition(position);
        Log.d("onItemClick: ", String.valueOf(position));
        Intent intent = new Intent(this, pinned_indian_states_details_activity.class);
        intent.putExtra("stateName", cursor.getString(cursor.getColumnIndex(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME)));
        intent.putExtra("position", cursor.getInt(cursor.getColumnIndex(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_POSITION)));
        startActivity(intent);
    }
}
