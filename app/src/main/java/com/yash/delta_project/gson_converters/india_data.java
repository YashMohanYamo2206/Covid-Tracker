package com.yash.delta_project.gson_converters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class india_data {
    @SerializedName("cases")
    @Expose
    private int cases;
    @SerializedName("deaths")
    @Expose
    private int deaths;
    @SerializedName("recovered")
    @Expose
    private int recovered;
    @SerializedName("active")
    @Expose
    private int active;

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }
}
