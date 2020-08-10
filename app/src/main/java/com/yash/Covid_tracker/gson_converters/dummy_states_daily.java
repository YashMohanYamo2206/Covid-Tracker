package com.yash.Covid_tracker.gson_converters;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class dummy_states_daily {
    @SerializedName("states_daily")
    List<indian_state_daily_details> indianStateDailyDetails;

    public List<indian_state_daily_details> getIndianStateDailyDetails() {
        return indianStateDailyDetails;
    }

}
