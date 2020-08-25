package com.yash.Covid_tracker.Activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_DataBase;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_contract;
import com.yash.Covid_tracker.R;
import com.yash.Covid_tracker.axis_formatter.X_axis_formatter__line_chart;
import com.yash.Covid_tracker.gson_converters.dummy_states_daily;
import com.yash.Covid_tracker.gson_converters.indian_state_daily_details;
import com.yash.Covid_tracker.interfaces.india_total_data;
import com.yash.Covid_tracker.retrofit_services.covid_api_services;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class pinned_indian_states_details_activity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    int position;
    long death = 0L;
    String stateName;
    List<Integer> colors = new ArrayList<>();
    List<PieEntry> entries = new ArrayList<>();
    ShimmerFrameLayout shimmerFrameLayout;
    TextView tv_stateName, tv_totalCases, tv_active, tv_recovered, tv_deaths, tv_lastUpdated, tv_delta_totalCases, tv_delta_activeCases, tv_delta_recoveredCases, tv_delta_deathCases;
    List<indian_state_daily_details> confirmed = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    List<indian_state_daily_details> recovered = new ArrayList<>();
    List<indian_state_daily_details> deaths = new ArrayList<>();
    ImageView iv_arrow_totalCases, iv_arrow_activeCases, iv_arrow_recoveredCases, iv_arrow_deathCases;
    BottomSheetBehavior bottomSheetBehavior;
    PieChart PieChart;
    LineChart daily_confirmed_cases, active_cases, recovered_cases, death_cases;
    List<Long> confirmed_data = new ArrayList<>();
    List<Long> recovered_data = new ArrayList<>();
    List<Long> deaths_data = new ArrayList<>();
    List<Long> active_data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indian_state_cases_details);
        tv_stateName = findViewById(R.id.state_name);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        stateName = intent.getStringExtra("stateName");
        tv_stateName.setText(stateName);
        pinned_indian_states_DataBase db = new pinned_indian_states_DataBase(this);
        mDatabase = db.getWritableDatabase();
        initialise_views();
        init();
    }

    @SuppressLint("SetTextI18n")
    private void initialise_views() {
        PieChart = findViewById(R.id.state_pie_chart);
        shimmerFrameLayout = findViewById(R.id.shimmer_country);
        tv_totalCases = findViewById(R.id.total_cases);
        tv_active = findViewById(R.id.active);
        tv_deaths = findViewById(R.id.deaths);
        tv_recovered = findViewById(R.id.recovered);
        tv_lastUpdated = findViewById(R.id.last_updated);
        //tv_lastUpdated.setText(tv_lastUpdated.getText() + " " + list_of_all_indian_states.dates.get(list_of_all_indian_states.dates.size() - 1));
        tv_delta_totalCases = findViewById(R.id.delta_total_cases);
        tv_delta_activeCases = findViewById(R.id.delta_active_cases);
        tv_delta_recoveredCases = findViewById(R.id.delta_recovered_cases);
        tv_delta_deathCases = findViewById(R.id.delta_death_cases);
        iv_arrow_totalCases = findViewById(R.id.arrow_total_cases);
        iv_arrow_activeCases = findViewById(R.id.arrow_active_cases);
        iv_arrow_recoveredCases = findViewById(R.id.arrow_recovered_cases);
        iv_arrow_deathCases = findViewById(R.id.arrow_death_cases);

        Button button = findViewById(R.id.pin_unpin);
        button.setVisibility(View.VISIBLE);
        if (CheckIsDataAlreadyInDBorNot(stateName)) {
            button.setText("UNPIN");
        } else {
            button.setText("PIN");
        }

        View bottom_sheet = findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);


        daily_confirmed_cases = findViewById(R.id.daily_new_cases_line_chart);
        active_cases = findViewById(R.id.active_cases_line_chart);
        recovered_cases = findViewById(R.id.recovered_cases_line_chart);
        death_cases = findViewById(R.id.death_cases_line_chart);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    findViewById(R.id.layout_bottom_sheet).setBackground(getDrawable(R.drawable.bg_bottom_sheet_completely_open));
                    findViewById(R.id.img_drag).setBackground(getDrawable(R.drawable.bg_bottom_sheet_drag_completely_open));
                } else {
                    findViewById(R.id.layout_bottom_sheet).setBackground(getDrawable(R.drawable.bg_bottom_sheet));
                    findViewById(R.id.img_drag).setBackground(getDrawable(R.drawable.bg_bottom_sheet_drag));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void init() {
        india_total_data covid19Interface = covid_api_services.get_indian_states_data().create(india_total_data.class);
        covid19Interface.getIndianStatesData().enqueue(new Callback<dummy_states_daily>() {
            @Override
            public void onResponse(@NotNull Call<dummy_states_daily> call, @NotNull Response<dummy_states_daily> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(pinned_indian_states_details_activity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<indian_state_daily_details> indian_state_daily_details;
                assert response.body() != null;
                indian_state_daily_details = response.body().getIndianStateDailyDetails();
                for (int i = 0; i < indian_state_daily_details.size(); i++) {
                    if ((i + 1) % 3 == 1) {
                        confirmed.add(indian_state_daily_details.get(i));
                    } else if ((i + 1) % 3 == 2) {
                        recovered.add(indian_state_daily_details.get(i));
                    } else {
                        deaths.add(indian_state_daily_details.get(i));
                    }
                }
                setCasesData(confirmed, recovered, deaths);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NotNull Call<dummy_states_daily> call, @NotNull Throwable t) {
                Toast.makeText(pinned_indian_states_details_activity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                Log.d("onFailure: ", Objects.requireNonNull(t.getMessage()));
                tv_totalCases.setText(tv_totalCases.getText() + "NULL");
                shimmerFrameLayout.hideShimmer();
                tv_active.setText(tv_active.getText() + "NULL");
                tv_deaths.setText(tv_deaths.getText() + "NULL");
                tv_recovered.setText(tv_recovered.getText() + "NULL");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void pin_unpin(View view) {
        System.out.println(CheckIsDataAlreadyInDBorNot(stateName));
        if (!CheckIsDataAlreadyInDBorNot(stateName)) {
            ContentValues cv = new ContentValues();
            cv.put(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME, stateName);
            cv.put(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_POSITION, position);
            Toast.makeText(this, stateName + " pinned " + position, Toast.LENGTH_SHORT).show();
            mDatabase.insert(pinned_indian_states_contract.pinned_indian_states_entry.TABLE_NAME, null, cv);
            Button button = findViewById(R.id.pin_unpin);
            button.setText("UNPIN");
        } else {
            Button button = findViewById(R.id.pin_unpin);
            button.setText("PIN");
            Toast.makeText(this, stateName + " unpinned ", Toast.LENGTH_SHORT).show();
            mDatabase.delete(pinned_indian_states_contract.pinned_indian_states_entry.TABLE_NAME, pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME + "=?", new String[]{stateName});
        }
    }

    public boolean CheckIsDataAlreadyInDBorNot(String stateName) {
        String Query = "SELECT EXISTS (SELECT * FROM " + pinned_indian_states_contract.pinned_indian_states_entry.TABLE_NAME + " WHERE " + pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME + "='" + stateName + "' LIMIT 1)";
        Cursor cursor = mDatabase.rawQuery(Query, null);
        cursor.moveToFirst();

        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;

    }

    public void back_button(View view) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void setCasesData(List<indian_state_daily_details> confirmed,
                             List<indian_state_daily_details> recovered,
                             List<indian_state_daily_details> deaths) {

        active_data = new ArrayList<>();
        confirmed_data = new ArrayList<>();
        recovered_data = new ArrayList<>();
        deaths_data = new ArrayList<>();

        try {

            for (indian_state_daily_details state_daily_details : confirmed) {
                dates.add(state_daily_details.getDate());
            }

            if (position == 0) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getAn()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getAn());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getAn());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getAn() - recovered.get(i).getAn() - deaths.get(i).getAn());
                }
            }
            if (position == 1) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getAp()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getAp());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getAp());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getAp() - recovered.get(i).getAp() - deaths.get(i).getAp());
                }
            }
            if (position == 2) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getAr()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getAr());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getAr());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getAr() - recovered.get(i).getAr() - deaths.get(i).getAr());
                }
            }
            if (position == 3) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getAs()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getAs());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getAs());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getAs() - recovered.get(i).getAs() - deaths.get(i).getAs());
                }
            }
            if (position == 4) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getBr()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getBr());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getBr());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getBr() - recovered.get(i).getBr() - deaths.get(i).getBr());
                }
            }
            if (position == 5) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getCh()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getCh());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getCh());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getCh() - recovered.get(i).getCh() - deaths.get(i).getCh());
                }
            }
            if (position == 6) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getCt()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getCt());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getCt());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getCt() - recovered.get(i).getCt() - deaths.get(i).getCt());
                }
            }
            if (position == 7) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getDd()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getDd());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getDd());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getDd() - recovered.get(i).getDd() - deaths.get(i).getDd());
                }
            }
            if (position == 8) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getDl()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getDl());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getDl());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getDl() - recovered.get(i).getDl() - deaths.get(i).getDl());
                }
            }
            if (position == 9) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getDn()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getDn());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getDn());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getDn() - recovered.get(i).getDn() - deaths.get(i).getDn());
                }
            }
            if (position == 10) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getGa()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getGa());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getGa());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getGa() - recovered.get(i).getGa() - deaths.get(i).getGa());
                }
            }
            if (position == 11) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getGj()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getGj());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getGj());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getGj() - recovered.get(i).getGj() - deaths.get(i).getGj());
                }
            }
            if (position == 12) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getHp()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getHp());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getHp());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getHp() - recovered.get(i).getHp() - deaths.get(i).getHp());
                }
            }
            if (position == 13) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getHr()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getHr());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getHr());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getHr() - recovered.get(i).getHr() - deaths.get(i).getHr());
                }
            }
            if (position == 14) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getJh()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getJh());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getJh());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getJh() - recovered.get(i).getJh() - deaths.get(i).getJh());
                }
            }
            if (position == 15) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getJk()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getJk());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getJk());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getJk() - recovered.get(i).getJk() - deaths.get(i).getJk());
                }
            }
            if (position == 16) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getKa()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getKa());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getKa());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getKa() - recovered.get(i).getKa() - deaths.get(i).getKa());
                }
            }
            if (position == 17) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getKl()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getKl());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getKl());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getKl() - recovered.get(i).getKl() - deaths.get(i).getKl());
                }
            }
            if (position == 18) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getLa()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getLa());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getLa());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getLa() - recovered.get(i).getLa() - deaths.get(i).getLa());
                }
            }
            if (position == 19) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getLd()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getLd());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getLd());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getLd() - recovered.get(i).getLd() - deaths.get(i).getLd());
                }
            }
            if (position == 20) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getMh()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getMh());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getMh());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getMh() - recovered.get(i).getMh() - deaths.get(i).getMh());
                }
            }
            if (position == 21) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getMl()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getMl());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getMl());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getMl() - recovered.get(i).getMl() - deaths.get(i).getMl());
                }
            }
            if (position == 22) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getMn()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getMn());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getMn());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getMn() - recovered.get(i).getMn() - deaths.get(i).getMn());
                }
            }
            if (position == 23) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getMp()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getMp());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getMp());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getMp() - recovered.get(i).getMp() - deaths.get(i).getMp());
                }
            }
            if (position == 24) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getMz()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getMz());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getMz());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getMz() - recovered.get(i).getMz() - deaths.get(i).getMz())
                    ;
                }
            }
            if (position == 25) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getNl()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getNl());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getNl());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getNl() - recovered.get(i).getNl() - deaths.get(i).getNl())
                    ;
                }
            }
            if (position == 26) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getOr()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getOr());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getOr());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getOr() - recovered.get(i).getOr() - deaths.get(i).getOr())
                    ;
                }
            }
            if (position == 27) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getPb()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getPb());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getPb());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getPb() - recovered.get(i).getPb() - deaths.get(i).getPb())
                    ;
                }
            }
            if (position == 28) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getPy()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getPy());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getPy());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getPy() - recovered.get(i).getPy() - deaths.get(i).getPy())
                    ;
                }
            }
            if (position == 29) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getRj()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getRj());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getRj());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getRj() - recovered.get(i).getRj() - deaths.get(i).getRj())
                    ;
                }
            }
            if (position == 30) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getSk()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getSk());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getSk());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getSk() - recovered.get(i).getSk() - deaths.get(i).getSk())
                    ;
                }
            }
            if (position == 31) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getTg()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getTg());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getTg());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getTg() - recovered.get(i).getTg() - deaths.get(i).getTg())
                    ;
                }
            }
            if (position == 32) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getTn()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getTn());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getTn());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getTn() - recovered.get(i).getTn() - deaths.get(i).getTn())
                    ;
                }
            }
            if (position == 33) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getTr()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getTr());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getTr());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getTr() - recovered.get(i).getTr() - deaths.get(i).getTr())
                    ;
                }
            }
            if (position == 34) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getUp()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getUp());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getUp());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getUp() - recovered.get(i).getUp() - deaths.get(i).getUp())
                    ;
                }
            }
            if (position == 35) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getUt()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getUt());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getUt());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getUt() - recovered.get(i).getUt() - deaths.get(i).getUt())
                    ;
                }
            }
            if (position == 36) {
                for (indian_state_daily_details state_daily_details : confirmed) {
                    confirmed_data.add(Math.abs(state_daily_details.getWb()));
                }
                for (indian_state_daily_details state_daily_details : recovered) {
                    recovered_data.add(state_daily_details.getWb());
                }
                for (indian_state_daily_details state_daily_details : deaths) {
                    deaths_data.add(state_daily_details.getWb());
                }
                for (int i = 0; i < confirmed_data.size(); i++) {
                    active_data.add(confirmed.get(i).getWb() - recovered.get(i).getWb() - deaths.get(i).getWb())
                    ;
                }
            }
            tv_lastUpdated.setText(tv_lastUpdated.getText() + " " + list_of_all_indian_states.dates.get(list_of_all_indian_states.dates.size() - 1));
            initialise_graphs(confirmed_data, recovered_data, deaths_data, active_data);
            for (long rec : confirmed_data)
                Log.d("rec: ", String.valueOf(rec));
            for (long rec : active_data)
                Log.d("act: ", String.valueOf(rec));
            for (long rec : deaths_data)
                Log.d("ded: ", String.valueOf(rec));
        } catch (Exception e) {
            Log.d("exc", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
        }
    }


    private void initialise_graphs(List<Long> confirmed_data,
                                   List<Long> recovered_data,
                                   List<Long> deaths_data,
                                   List<Long> active_data) {
        display_daily_cases(confirmed_data, list_of_all_indian_states.dates);
        display_recovered_cases(recovered_data, list_of_all_indian_states.dates);
        display_death_cases(deaths_data, list_of_all_indian_states.dates);
        display_active_cases(active_data, list_of_all_indian_states.dates);
        shimmerFrameLayout.hideShimmer();
        display_pie_chart();
    }

    private void display_pie_chart() {
        colors.add(Color.YELLOW);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        entries.add(new PieEntry(death, "Deaths"));
        PieDataSet data_set = new PieDataSet(entries, "");
        data_set.setValueTextSize(13f);
        data_set.setColors(colors);
        data_set.setDrawValues(false);
        PieData pieData = new PieData(data_set);
        PieChart.setDrawEntryLabels(false);
        Description description = new Description();
        description.setText((String) tv_stateName.getText());
        description.setTextColor(Color.YELLOW);
        description.setTextSize(13f);
        PieChart.setHoleRadius(1f);
        PieChart.setDragDecelerationFrictionCoef(0.5f);
        PieChart.setDescription(description);
        PieChart.setData(pieData);
        PieChart.animateY(1000);
        PieChart.invalidate();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void display_recovered_cases(@NotNull List<Long> recovered, List<String> date) {

        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < recovered.size(); i++) {
            sum += recovered.get(i);
            cumulative.add(sum);
            Log.d("recovered_cases: ", String.valueOf(sum));
        }
        recovered = cumulative;
        entries.add(new PieEntry(cumulative.get(cumulative.size() - 1), "Recovered cases"));
        tv_recovered.setText(tv_recovered.getText() + String.valueOf(recovered.get(recovered.size() - 1)));
        tv_delta_recoveredCases.setText(String.valueOf(Math.abs(recovered.get(recovered.size() - 1) - recovered.get(recovered.size() - 2))));
        iv_arrow_recoveredCases.setImageDrawable((recovered.get(recovered.size() - 1) - recovered.get(recovered.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));

        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < recovered.size(); i++) {
            entries.add(new Entry(i, recovered.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "recovered cases".toUpperCase());
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        lineDataSet.setLineWidth(1f);
        XAxis axis = recovered_cases.getXAxis();
        axis.setTextColor(Color.WHITE);
        axis.setLabelCount(10, true);
        axis.setValueFormatter(new X_axis_formatter__line_chart(date));
        lineDataSet.setDrawCircles(false);
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        recovered_cases.setData(data);
        recovered_cases.invalidate();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void display_active_cases(@NotNull List<Long> active, List<String> date) {

        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < active.size(); i++) {
            sum += active.get(i);
            cumulative.add(sum);
        }
        active = cumulative;
        entries.add(new PieEntry(active.get(active.size() - 1), "Active cases"));
        tv_active.setText(tv_active.getText() + String.valueOf(active.get(active.size() - 1)));
        tv_delta_activeCases.setText(String.valueOf(Math.abs(active.get(active.size() - 1) - active.get(active.size() - 2))));
        iv_arrow_activeCases.setImageDrawable((active.get(active.size() - 1) - active.get(active.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));

        for (int i = 0; i < active.size(); i++) {
            Log.d("display_active_cases: ", String.valueOf(active.get(i)));
        }
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < active.size(); i++) {
            entries.add(new Entry(i, active.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "ACTIVE CASES");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        XAxis axis = active_cases.getXAxis();
        axis.setTextColor(Color.WHITE);
        axis.setLabelCount(10, true);
        axis.setValueFormatter(new X_axis_formatter__line_chart(date));
        lineDataSet.setLineWidth(1f);
        lineDataSet.setDrawCircles(false);
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        active_cases.setData(data);
        active_cases.invalidate();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void display_death_cases(@NotNull List<Long> deaths, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < deaths.size(); i++) {
            sum += deaths.get(i);
            cumulative.add(sum);
        }
        deaths = cumulative;
        death = sum;
        tv_deaths.setText(tv_deaths.getText() + String.valueOf(deaths.get(deaths.size() - 1)));
        tv_delta_deathCases.setText(String.valueOf(Math.abs(deaths.get(deaths.size() - 1) - deaths.get(deaths.size() - 2))));
        iv_arrow_deathCases.setImageDrawable((deaths.get(deaths.size() - 1) - deaths.get(deaths.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));

        for (int i = 0; i < deaths.size(); i++) {
            entries.add(new Entry(i, deaths.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "DEATHS CASES");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        lineDataSet.setLineWidth(1f);
        XAxis axis = death_cases.getXAxis();
        axis.setTextColor(Color.WHITE);
        axis.setLabelCount(10, true);
        axis.setValueFormatter(new X_axis_formatter__line_chart(date));
        dataSets.add(lineDataSet);
        lineDataSet.setDrawCircles(false);
        LineData data = new LineData(dataSets);
        death_cases.setData(data);
        death_cases.invalidate();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void display_daily_cases(@NotNull List<Long> total_cases, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < total_cases.size() - 1; i++) {
            sum += total_cases.get(i);
            cumulative.add(sum);
        }
        tv_totalCases.setText((cumulative.get(cumulative.size() - 1) > 0) ? String.valueOf(cumulative.get(cumulative.size() - 1)) : "NULL");
        tv_delta_totalCases.setText(String.valueOf(Math.abs(cumulative.get(cumulative.size() - 1) - cumulative.get(cumulative.size() - 2))));
        iv_arrow_totalCases.setImageDrawable((total_cases.get(total_cases.size() - 1) - total_cases.get(total_cases.size() - 2) == 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));
        for (int i = 0; i < total_cases.size() - 1; i++) {
            entries.add(new Entry(i, total_cases.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "DAILY CASES");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        XAxis axis = daily_confirmed_cases.getXAxis();
        axis.setTextColor(Color.WHITE);
        axis.setLabelCount(10, true);
        axis.setValueFormatter(new X_axis_formatter__line_chart(date));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setLineWidth(1f);
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        daily_confirmed_cases.setData(data);
        daily_confirmed_cases.invalidate();
    }

}

