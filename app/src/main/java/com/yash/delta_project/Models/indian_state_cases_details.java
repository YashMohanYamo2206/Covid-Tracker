package com.yash.delta_project.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.yash.delta_project.R;
import com.yash.delta_project.axis_formatter.X_axis_formatter__line_chart;
import com.yash.delta_project.gson_converters.countries_details;
import com.yash.delta_project.interfaces.countries_interface;
import com.yash.delta_project.retrofit_services.covid_api_services;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class indian_state_cases_details extends AppCompatActivity {
    long death= 0L;
    List<Integer> colors = new ArrayList<>();
    PieChart PieChart;
    LineChart daily_confirmed_cases, active_cases, recovered_cases, death_cases;
    TextView tv_countryName, tv_totalCases, tv_active, tv_recovered, tv_deaths, tv_lastUpdated;
    CoordinatorLayout coordinatorLayout;
    BottomSheetBehavior bottomSheetBehavior;
    ShimmerFrameLayout shimmerFrameLayout;
    List<PieEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indian_state_cases_details);
        tv_countryName = findViewById(R.id.country_name);
        Intent intent = getIntent();
        tv_countryName.setText(Objects.requireNonNull(intent.getStringExtra("stateName")).toUpperCase());
        initialise_views();
        initialise_graphs();
    }

    private void initialise_views() {
        PieChart = findViewById(R.id.state_pie_chart);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        shimmerFrameLayout = findViewById(R.id.shimmer_country);
        tv_totalCases = findViewById(R.id.total_cases);
        tv_totalCases.getLayoutParams().width = coordinatorLayout.getWidth() / 2 - 10;
        tv_active = findViewById(R.id.active);
        tv_active.getLayoutParams().height = (coordinatorLayout.getWidth() / 2 - 10);
        tv_deaths = findViewById(R.id.deaths);
        tv_deaths.getLayoutParams().height = (coordinatorLayout.getWidth() / 2 - 10);
        tv_recovered = findViewById(R.id.recovered);
        tv_recovered.getLayoutParams().height = (coordinatorLayout.getWidth() / 2 - 10);
        tv_lastUpdated = findViewById(R.id.last_updated);
        tv_lastUpdated.setText(tv_lastUpdated.getText()+" "+list_of_all_indian_states.dates.get(list_of_all_indian_states.dates.size()-1));
        View bottom_sheet = findViewById(R.id.layout_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        // bottom_sheet.getLayoutParams().height = (int)tv_countryName.getY() + 20;


        daily_confirmed_cases = findViewById(R.id.daily_new_cases_line_chart);
        active_cases = findViewById(R.id.active_cases_line_chart);
        recovered_cases = findViewById(R.id.recovered_cases_line_chart);
        death_cases = findViewById(R.id.death_cases_line_chart);

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
        entries.add(new PieEntry(death,"Deaths"));
        PieDataSet data_set = new PieDataSet(entries, "");
        data_set.setValueTextSize(13f);
        data_set.setColors(colors);
        PieData pieData = new PieData(data_set);
        PieChart.setDrawEntryLabels(false);
        Description description = new Description();
        description.setText((String) tv_countryName.getText());
        description.setTextColor(Color.YELLOW);
        description.setTextSize(13f);
        PieChart.setDescription(description);
        PieChart.setData(pieData);
        PieChart.animateY(1500);
        PieChart.invalidate();
    }


    private void display_recovered_cases(@NotNull List<Long> recovered, List<String> date) {

        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < recovered.size(); i++) {
            sum += recovered.get(i);
            cumulative.add(sum);
            Log.d("recovered_cases: ", String.valueOf(sum));
        }
        recovered = new ArrayList<>();
        recovered = cumulative;
        entries.add(new PieEntry(recovered.get(recovered.size() - 1), "Recovered cases"));
        tv_recovered.setText(tv_recovered.getText() + String.valueOf(recovered.get(recovered.size() - 1)));
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

    private void display_active_cases(@NotNull List<Long> active, List<String> date) {

        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < active.size(); i++) {
            sum += active.get(i);
            cumulative.add(sum);
        }
        active = new ArrayList<>();
        active = cumulative;
        entries.add(new PieEntry(active.get(active.size() - 1), "Active cases"));
        tv_active.setText(tv_active.getText() + String.valueOf(active.get(active.size() - 1)));
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

    private void display_death_cases(@NotNull List<Long> deaths, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < deaths.size(); i++) {
            sum += deaths.get(i);
            cumulative.add(sum);
        }
        deaths = new ArrayList<>();
        deaths = cumulative;
        death = sum;
        //entries.add(new PieEntry(sum, "Death cases"));
        tv_deaths.setText(tv_deaths.getText() + String.valueOf(deaths.get(deaths.size() - 1)));
        for (int i = 0; i < deaths.size(); i++) {
            entries.add(new Entry(i, deaths.get(i)));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, "DEATHS CASES");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //lineDataSet.setColor(R.color.color_death);
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

    @SuppressLint("SetTextI18n")
    private void display_daily_cases(@NotNull List<Long> total_cases, List<String> date) {
        ArrayList<Entry> entries = new ArrayList<>();
        List<Long> cumulative = new ArrayList<>();
        long sum = 0;
        for (int i = 0; i < total_cases.size(); i++) {
            sum += total_cases.get(i);
            cumulative.add(sum);
        }
        tv_totalCases.setText(tv_totalCases.getText() + String.valueOf(cumulative.get(cumulative.size() - 1)));
        for (int i = 0; i < total_cases.size() - 1; i++) {
            entries.add(new Entry(i, total_cases.get(i)));
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
}