/**
 * @param Example_input
 * {
 * {
 * "an": "0",
 * "ap": "1",
 * "ar": "0",
 * "as": "0",
 * "br": "0",
 * "ch": "0",
 * "ct": "0",
 * "date": "14-Mar-20",
 * "dd": "0",
 * "dl": "7",
 * "dn": "0",
 * "ga": "0",
 * "gj": "0",
 * "hp": "0",
 * "hr": "14",
 * "jh": "0",
 * "jk": "2",
 * "ka": "6",
 * "kl": "19",
 * "la": "0",
 * "ld": "0",
 * "mh": "14",
 * "ml": "0",
 * "mn": "0",
 * "mp": "0",
 * "mz": "0",
 * "nl": "0",
 * "or": "0",
 * "pb": "1",
 * "py": "0",
 * "rj": "3",
 * "sk": "0",
 * "status": "Confirmed",
 * "tg": "1",
 * "tn": "1",
 * "tr": "0",
 * "tt": "81",
 * "un": "0",
 * "up": "12",
 * "ut": "0",
 * "wb": "0"
 * }, {....}, ...
 * }
 */
package com.yash.Covid_tracker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.yash.Covid_tracker.Adapters.indian_states_list_adapter;
import com.yash.Covid_tracker.R;
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

public class list_of_all_indian_states extends AppCompatActivity implements indian_states_list_adapter.OnItemClickListener {
    RecyclerView recyclerView;
    indian_states_list_adapter adapter;
    SearchView searchView;
    public static List<String> dates = new ArrayList<>();
    List<String> states = new ArrayList<>();
    TextView loading, indian_states;
    ShimmerFrameLayout shimmerFrameLayout;
    List<indian_state_daily_details> confirmed;
    List<indian_state_daily_details> recovered;
    List<indian_state_daily_details> deaths;
    public static List<Long> confirmed_data = new ArrayList<>();
    public static List<Long> recovered_data = new ArrayList<>();
    public static List<Long> deaths_data = new ArrayList<>();
    public static List<Long> active_data = new ArrayList<>();
    //List<List<String>> STATES = new ArrayList<>();
    //List<List<Long>> state_cases_details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);
        loading = findViewById(R.id.loading);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        searchView = findViewById(R.id.search_view);
        searchView.setVisibility(View.GONE);
        init_recyclerview();
        init();
    }

    private void init_recyclerview() {
        indian_states = findViewById(R.id.indian_states);
        indian_states.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        states = getStates().get(0);
        adapter = new indian_states_list_adapter(states);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        //recyclerView.setVisibility(View.VISIBLE);

        confirmed = new ArrayList<>();
        recovered = new ArrayList<>();
        deaths = new ArrayList<>();
        //searchView.setVisibility(View.VISIBLE);
    }

    private void init() {
        india_total_data covid19Interface = covid_api_services.get_indian_states_data().create(india_total_data.class);
        covid19Interface.getIndianStatesData().enqueue(new Callback<dummy_states_daily>() {
            @Override
            public void onResponse(@NotNull Call<dummy_states_daily> call, @NotNull Response<dummy_states_daily> response) {
                if (!response.isSuccessful()) {
                    loading.setText(R.string.network_error_message);
                    shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 1000);
                    Toast.makeText(list_of_all_indian_states.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                loading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
          //    searchView.setVisibility(View.VISIBLE);
                List<indian_state_daily_details> indian_state_daily_details = new ArrayList<>();
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
            }

            @Override
            public void onFailure(@NotNull Call<dummy_states_daily> call, @NotNull Throwable t) {
                loading.setText(R.string.network_error_message);
                Toast.makeText(list_of_all_indian_states.this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
                Log.d("onFailure: ", Objects.requireNonNull(t.getMessage()));
                shimmerFrameLayout.postDelayed(() -> shimmerFrameLayout.hideShimmer(), 1000);
            }
        });
    }

    @NotNull
    private List<List<String>> getStates() {
        List<List<String>> STATES = new ArrayList<>();
        List<String> states = new ArrayList<>();
        states.add("andaman and  nicobar");
        states.add("andhra pradesh");
        states.add("arunachal pradesh");
        states.add("assam");
        states.add("bihar");
        states.add("chandigarh");
        states.add("chattisgarh");
        states.add("daman and diu");
        states.add("delhi");
        states.add("dadra and nagar haveli");
        states.add("goa");
        states.add("gujarat");
        states.add("himachal pradesh");
        states.add("haryana");
        states.add("jharkhand");
        states.add("jammu and kashmir");
        states.add("karnataka");
        states.add("kerala");
        states.add("ladakh");
        states.add("lakshadweep");
        states.add("maharashtra");
        states.add("meghalaya");
        states.add("manipur");
        states.add("madhya pradesh");
        states.add("mizoram");
        states.add("nagaland");
        states.add("odisha");
        states.add("punjab");
        states.add("pondicherry");
        states.add("rajasthan");
        states.add("sikkim");
        states.add("telangana");
        states.add("tamil nadu");
        states.add("tripura");
        states.add("uttar pradesh");
        states.add("uttrakhand");
        states.add("west bengal");
        STATES.add(states);
        return STATES;
    }

    @Override
    public void onItemClick(int position) {
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
                    active_data.add(Math.max(confirmed.get(i).getAn() - recovered.get(i).getAn() - deaths.get(i).getAn(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getAp() - recovered.get(i).getAp() - deaths.get(i).getAp(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getAr() - recovered.get(i).getAr() - deaths.get(i).getAr(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getAs() - recovered.get(i).getAs() - deaths.get(i).getAs(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getBr() - recovered.get(i).getBr() - deaths.get(i).getBr(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getCh() - recovered.get(i).getCh() - deaths.get(i).getCh(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getCt() - recovered.get(i).getCt() - deaths.get(i).getCt(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getDd() - recovered.get(i).getDd() - deaths.get(i).getDd(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getDl() - recovered.get(i).getDl() - deaths.get(i).getDl(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getDn() - recovered.get(i).getDn() - deaths.get(i).getDn(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getGa() - recovered.get(i).getGa() - deaths.get(i).getGa(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getGj() - recovered.get(i).getGj() - deaths.get(i).getGj(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getHp() - recovered.get(i).getHp() - deaths.get(i).getHp(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getHr() - recovered.get(i).getHr() - deaths.get(i).getHr(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getJh() - recovered.get(i).getJh() - deaths.get(i).getJh(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getJk() - recovered.get(i).getJk() - deaths.get(i).getJk(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getKa() - recovered.get(i).getKa() - deaths.get(i).getKa(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getKl() - recovered.get(i).getKl() - deaths.get(i).getKl(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getLa() - recovered.get(i).getLa() - deaths.get(i).getLa(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getLd() - recovered.get(i).getLd() - deaths.get(i).getLd(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getMh() - recovered.get(i).getMh() - deaths.get(i).getMh(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getMl() - recovered.get(i).getMl() - deaths.get(i).getMl(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getMn() - recovered.get(i).getMn() - deaths.get(i).getMn(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getMp() - recovered.get(i).getMp() - deaths.get(i).getMp(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getMz() - recovered.get(i).getMz() - deaths.get(i).getMz(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getNl() - recovered.get(i).getNl() - deaths.get(i).getNl(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getOr() - recovered.get(i).getOr() - deaths.get(i).getOr(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getPb() - recovered.get(i).getPb() - deaths.get(i).getPb(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getPy() - recovered.get(i).getPy() - deaths.get(i).getPy(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getRj() - recovered.get(i).getRj() - deaths.get(i).getRj(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getSk() - recovered.get(i).getSk() - deaths.get(i).getSk(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getTg() - recovered.get(i).getTg() - deaths.get(i).getTg(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getTn() - recovered.get(i).getTn() - deaths.get(i).getTn(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getTr() - recovered.get(i).getTr() - deaths.get(i).getTr(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getUp() - recovered.get(i).getUp() - deaths.get(i).getUp(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getUt() - recovered.get(i).getUt() - deaths.get(i).getUt(), 0));
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
                    active_data.add(Math.max(confirmed.get(i).getWb() - recovered.get(i).getWb() - deaths.get(i).getWb(), 0));
                }
            }
            for (long rec : confirmed_data)
                Log.d("rec: ", String.valueOf(rec));
            for (long rec : active_data)
                Log.d("act: ", String.valueOf(rec));
            for (long rec : deaths_data)
                Log.d("ded: ", String.valueOf(rec));
            Intent intent = new Intent(this, indian_state_cases_details.class);
            intent.putExtra("stateName", states.get(position));
            startActivity(intent);
        } catch (Exception e) {
            Log.d("exc", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, R.string.network_error_message, Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, String.valueOf(position) + states.get(position), Toast.LENGTH_SHORT).show();
    }
}

