package com.yash.Covid_tracker.gson_converters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class global {
    @SerializedName("NewConfirmed")
    @Expose
    private int newConfirmed;
    @SerializedName("TotalConfirmed")
    @Expose
    private int totalConfirmed;
    @SerializedName("NewDeaths")
    @Expose
    private int newDeaths;
    @SerializedName("TotalDeaths")
    @Expose
    private int totalDeaths;
    @SerializedName("NewRecovered")
    @Expose
    private int newRecovered;
    @SerializedName("TotalRecovered")
    @Expose
    private int totalRecovered;

    public int getNewConfirmed() {
        return newConfirmed;
    }

    public void setNewConfirmed(int newConfirmed) {
        this.newConfirmed = newConfirmed;
    }

    public int getTotalConfirmed() {
        return totalConfirmed;
    }

    public void setTotalConfirmed(int totalConfirmed) {
        this.totalConfirmed = totalConfirmed;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(int newDeaths) {
        this.newDeaths = newDeaths;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public int getNewRecovered() {
        return newRecovered;
    }

    public void setNewRecovered(int newRecovered) {
        this.newRecovered = newRecovered;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(int totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

}
