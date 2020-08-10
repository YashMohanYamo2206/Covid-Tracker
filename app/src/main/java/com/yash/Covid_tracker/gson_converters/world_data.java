package com.yash.Covid_tracker.gson_converters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class world_data {
    @SerializedName("Global")
    @Expose
    private global global;

    public global getGlobal() {
        return global;
    }

    public void setGlobal(global global) {
        this.global = global;
    }
}
