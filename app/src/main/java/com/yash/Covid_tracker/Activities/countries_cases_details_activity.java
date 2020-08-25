package com.yash.Covid_tracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;
import com.yash.Covid_tracker.DataBase.pinned_countries_DataBase;
import com.yash.Covid_tracker.DataBase.pinned_countries_contract;
import com.yash.Covid_tracker.R;
import com.yash.Covid_tracker.axis_formatter.X_axis_formatter__line_chart;
import com.yash.Covid_tracker.gson_converters.countries_details;
import com.yash.Covid_tracker.interfaces.countries_interface;
import com.yash.Covid_tracker.retrofit_services.covid_api_services;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

@SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
public class countries_cases_details_activity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    String countryName, slug;
    String img_url;
    List<Integer> deaths = new ArrayList<>();
    List<Integer> recovered = new ArrayList<>();
    List<Integer> active = new ArrayList<>();
    List<Integer> total_cases = new ArrayList<>();
    List<String> date = new ArrayList<>();
    LineChart daily_confirmed_cases, active_cases, recovered_cases, death_cases;
    TextView tv_countryName, tv_totalCases, tv_active, tv_recovered, tv_deaths, tv_lastUpdated, tv_delta_totalCases, tv_delta_activeCases, tv_delta_recoveredCases, tv_delta_deathCases;
    ImageView flag, iv_arrow_totalCases, iv_arrow_activeCases, iv_arrow_recoveredCases, iv_arrow_deathCases;
    CoordinatorLayout coordinatorLayout;
    BottomSheetBehavior bottomSheetBehavior;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries_cases_details_activity);
        initialise_views();
        Intent intent = getIntent();
        countryName = intent.getStringExtra("countryName");
        slug = intent.getStringExtra("slug");
        initialise_retrofit_services(slug);
        pinned_countries_DataBase db = new pinned_countries_DataBase(this);
        mDatabase = db.getWritableDatabase();
        flag = findViewById(R.id.flag);
        img_url = intent.getStringExtra("imgUrl") ;
        Picasso.with(this).load("https://www.countryflags.io/" + img_url + "/shiny/64.png").into(flag);
    }

    private void initialise_views() {
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        shimmerFrameLayout = findViewById(R.id.shimmer_country);
        tv_countryName = findViewById(R.id.country_name);
        tv_totalCases = findViewById(R.id.total_cases);
        tv_active = findViewById(R.id.active);
        tv_deaths = findViewById(R.id.deaths);
        tv_recovered = findViewById(R.id.recovered);
        tv_lastUpdated = findViewById(R.id.last_updated);
        tv_delta_totalCases = findViewById(R.id.delta_total_cases);
        tv_delta_activeCases = findViewById(R.id.delta_active_cases);
        tv_delta_recoveredCases = findViewById(R.id.delta_recovered_cases);
        tv_delta_deathCases = findViewById(R.id.delta_death_cases);
        iv_arrow_totalCases = findViewById(R.id.arrow_total_cases);
        iv_arrow_activeCases = findViewById(R.id.arrow_active_cases);
        iv_arrow_recoveredCases = findViewById(R.id.arrow_recovered_cases);
        iv_arrow_deathCases = findViewById(R.id.arrow_death_cases);

        View bottom_sheet = findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        //bottom_sheet.getLayoutParams().height = (int)tv_countryName.getY() + 20;

        daily_confirmed_cases = findViewById(R.id.daily_new_cases_line_chart);
        active_cases = findViewById(R.id.active_cases_line_chart);
        recovered_cases = findViewById(R.id.recovered_cases_line_chart);
        death_cases = findViewById(R.id.death_cases_line_chart);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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


    private void initialise_retrofit_services(String s) {
        countries_interface api_services = covid_api_services.get_countries().create(countries_interface.class);
        api_services.getCountryDetail(s).enqueue(new Callback<List<countries_details>>() {
            @Override
            public void onResponse(@NonNull Call<List<countries_details>> call, @NonNull Response<List<countries_details>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(countries_cases_details_activity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                assert response.body() != null;
                if (response.body().isEmpty()) {
                    shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 500);
                    tv_totalCases.setText(tv_totalCases.getText() + "NULL");
                    tv_active.setText(tv_active.getText() + "NULL");
                    tv_deaths.setText(tv_deaths.getText() + "NULL");
                    tv_recovered.setText(tv_recovered.getText() + "NULL");
                    return;
                }
                Button button = findViewById(R.id.pin_unpin);
                button.setVisibility(View.VISIBLE);
                if (CheckIsDataAlreadyInDBorNot(countryName)) {
                    button.setText("UNPIN");
                } else {
                    button.setText("PIN");
                }
                shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 500);
                tv_countryName.setText(response.body().get(0).getCountry().toUpperCase());
                for (countries_details countries_details : response.body()) {
                    deaths.add(countries_details.getDeaths());
                    recovered.add(countries_details.getRecovered());
                    total_cases.add(countries_details.getConfirmed());
                    active.add(Math.max(countries_details.getConfirmed() - countries_details.getDeaths() - countries_details.getRecovered(), 0));
                    date.add(countries_details.getDate().substring(0, 10));
                }

                tv_totalCases.setText(String.valueOf(total_cases.get(total_cases.size() - 1)));


                tv_delta_totalCases.setText(String.valueOf(Math.abs(total_cases.get(total_cases.size() - 1) - total_cases.get(total_cases.size() - 2))));
                iv_arrow_totalCases.setImageDrawable((total_cases.get(total_cases.size() - 1) - total_cases.get(total_cases.size() - 2) == 0) ? null : (total_cases.get(total_cases.size() - 1) - total_cases.get(total_cases.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));


                tv_delta_activeCases.setText(String.valueOf(Math.abs(active.get(active.size() - 1) - active.get(active.size() - 2))));
                iv_arrow_activeCases.setImageDrawable((active.get(active.size() - 1) - active.get(active.size() - 2) == 0) ? null : (active.get(active.size() - 1) - active.get(active.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));


                tv_delta_recoveredCases.setText(String.valueOf(Math.abs(recovered.get(recovered.size() - 1) - recovered.get(recovered.size() - 2))));
                iv_arrow_recoveredCases.setImageDrawable((recovered.get(recovered.size() - 1) - recovered.get(recovered.size() - 2) == 0) ? null : (recovered.get(recovered.size() - 1) - recovered.get(recovered.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));


                tv_delta_deathCases.setText(String.valueOf(Math.abs(deaths.get(deaths.size() - 1) - deaths.get(deaths.size() - 2))));
                iv_arrow_deathCases.setImageDrawable((deaths.get(deaths.size() - 1) - deaths.get(deaths.size() - 2) == 0) ? null : (deaths.get(deaths.size() - 1) - deaths.get(deaths.size() - 2) < 0) ? getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24, null) : getResources().getDrawable(R.drawable.ic_increase, null));


                tv_active.setText(String.valueOf(active.get(active.size() - 1)));
                tv_deaths.setText(String.valueOf(deaths.get(deaths.size() - 1)));
                tv_recovered.setText(String.valueOf(recovered.get(recovered.size() - 1)));
                tv_lastUpdated.setText(tv_lastUpdated.getText() + " " + date.get(date.size() - 1));
                display_daily_cases(total_cases, date);
                display_recovered_cases(recovered, date);
                display_active_cases(active, date);
                display_death_cases(deaths, date);
            }

            @Override
            public void onFailure(@NonNull Call<List<countries_details>> call, @NonNull Throwable t) {
                Log.d("onFailure: ", Objects.requireNonNull(t.getMessage()));
                shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 500);
                tv_totalCases.setText(tv_totalCases.getText() + "NULL");
                tv_active.setText(tv_active.getText() + "NULL");
                tv_deaths.setText(tv_deaths.getText() + "NULL");
                tv_recovered.setText(tv_recovered.getText() + "NULL");
                Toast.makeText(countries_cases_details_activity.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void display_recovered_cases(@NotNull List<Integer> recovered, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < recovered.size(); i++) {
            entries.add(new Entry(i, recovered.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "recovered CASES".toUpperCase());
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        lineDataSet.setLineWidth(1f);
        XAxis axis = recovered_cases.getXAxis();
        axis.setTextColor(Color.WHITE);
        axis.setLabelCount(10, true);
        axis.setValueFormatter(new X_axis_formatter__line_chart(date));
        //lineDataSet.setColor(R.color.color_recovered);
        lineDataSet.setDrawCircles(false);
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        recovered_cases.setData(data);
        recovered_cases.invalidate();
    }

    private void display_active_cases(@NotNull List<Integer> active, List<String> date) {
        for (int i = 0; i < active.size(); i++) {
            Log.d("display_active_cases: ", date.get(i));
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

    private void display_death_cases(@NotNull List<Integer> deaths, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
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

    private void display_daily_cases(@NotNull List<Integer> total_cases, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < total_cases.size() - 1; i++) {
            if (i == 0) {
                entries.add(new Entry(0, total_cases.get(0)));
            } else {
                entries.add(new Entry(i, total_cases.get(i + 1) - total_cases.get(i)));
            }
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "DAILY CASES");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        XAxis axis = daily_confirmed_cases.getXAxis();
        //axis.enableAxisLineDashedLine(10f,3f,0f);
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


    public void back_button(View view) {
        finish();
    }

    public void pin_unpin(View view) {
        if (!CheckIsDataAlreadyInDBorNot(countryName)) {
            ContentValues cv = new ContentValues();
            cv.put(pinned_countries_contract.pinned_countries_entry.COLUMN_COUNTRY_NAME, countryName);
            cv.put(pinned_countries_contract.pinned_countries_entry.COLUMN_IMAGE_URL, img_url);
            cv.put(pinned_countries_contract.pinned_countries_entry.COLUMN_SLUG, slug);
            Toast.makeText(this, countryName + " pinned", Toast.LENGTH_SHORT).show();
            mDatabase.insert(pinned_countries_contract.pinned_countries_entry.TABLE_NAME, null, cv);
            Button button = findViewById(R.id.pin_unpin);
            button.setText("UNPIN");
        } else {
            Button button = findViewById(R.id.pin_unpin);
            button.setText("PIN");
            Toast.makeText(this, countryName + " unpinned", Toast.LENGTH_SHORT).show();
            mDatabase.delete(pinned_countries_contract.pinned_countries_entry.TABLE_NAME, pinned_countries_contract.pinned_countries_entry.COLUMN_COUNTRY_NAME + "=?", new String[]{countryName});
        }
    }

    public boolean CheckIsDataAlreadyInDBorNot(String countryName) {
        String Query = "SELECT EXISTS (SELECT * FROM " + pinned_countries_contract.pinned_countries_entry.TABLE_NAME + " WHERE " + pinned_countries_contract.pinned_countries_entry.COLUMN_COUNTRY_NAME + "='" + countryName + "' LIMIT 1)";
        Cursor cursor = mDatabase.rawQuery(Query, null);
        cursor.moveToFirst();

        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;

    }
}