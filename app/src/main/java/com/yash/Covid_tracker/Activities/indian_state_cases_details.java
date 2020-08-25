package com.yash.Covid_tracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import com.yash.Covid_tracker.DataBase.pinned_countries_contract;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_DataBase;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_contract;
import com.yash.Covid_tracker.R;
import com.yash.Covid_tracker.axis_formatter.X_axis_formatter__line_chart;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("SetTextI18n")
public class indian_state_cases_details extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    int position;
    String stateName;
    long death = 0L;
    List<Integer> colors = new ArrayList<>();
    PieChart PieChart;
    LineChart daily_confirmed_cases, active_cases, recovered_cases, death_cases;
    TextView tv_stateName, tv_totalCases, tv_active, tv_recovered, tv_deaths, tv_lastUpdated, tv_delta_totalCases, tv_delta_activeCases, tv_delta_recoveredCases, tv_delta_deathCases;
    ImageView iv_arrow_totalCases, iv_arrow_activeCases, iv_arrow_recoveredCases, iv_arrow_deathCases;
    CoordinatorLayout coordinatorLayout;
    BottomSheetBehavior bottomSheetBehavior;
    ShimmerFrameLayout shimmerFrameLayout;
    List<PieEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indian_state_cases_details);
        tv_stateName = findViewById(R.id.state_name);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        stateName = Objects.requireNonNull(intent.getStringExtra("stateName")).toUpperCase();
        tv_stateName.setText(stateName);
        pinned_indian_states_DataBase db = new pinned_indian_states_DataBase(this);
        mDatabase = db.getWritableDatabase();
        initialise_views();
        initialise_graphs();
    }

    private void initialise_views() {
        PieChart = findViewById(R.id.state_pie_chart);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        shimmerFrameLayout = findViewById(R.id.shimmer_country);
        tv_totalCases = findViewById(R.id.total_cases);
        tv_active = findViewById(R.id.active);
        tv_deaths = findViewById(R.id.deaths);
        tv_recovered = findViewById(R.id.recovered);
        tv_lastUpdated = findViewById(R.id.last_updated);
        tv_lastUpdated.setText(tv_lastUpdated.getText() + " " + list_of_all_indian_states.dates.get(list_of_all_indian_states.dates.size() - 1));
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

    private void initialise_graphs() {
        display_daily_cases(list_of_all_indian_states.confirmed_data, list_of_all_indian_states.dates);
        display_recovered_cases(list_of_all_indian_states.recovered_data, list_of_all_indian_states.dates);
        display_death_cases(list_of_all_indian_states.deaths_data, list_of_all_indian_states.dates);
        display_active_cases(list_of_all_indian_states.active_data, list_of_all_indian_states.dates);
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


    @SuppressLint("UseCompatLoadingForDrawables")
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

    @SuppressLint("UseCompatLoadingForDrawables")
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

    @SuppressLint("UseCompatLoadingForDrawables")
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

    public void back_button(View view) {
        finish();
    }

    public void pin_unpin(View view) {
        System.out.println(CheckIsDataAlreadyInDBorNot(stateName));
        //Toast.makeText(this, ""+CheckIsDataAlreadyInDBorNot(stateName), Toast.LENGTH_SHORT).show();
        if (!CheckIsDataAlreadyInDBorNot(stateName)) {
            ContentValues cv = new ContentValues();
            cv.put(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME, stateName);
            cv.put(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_POSITION, position);
            Toast.makeText(this, stateName + " pinned ", Toast.LENGTH_SHORT).show();
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
}