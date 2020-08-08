package com.yash.delta_project.axis_formatter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class X_axis_formatter__line_chart extends ValueFormatter {

    ArrayList<String> labels = new ArrayList<>();

    public X_axis_formatter__line_chart(ArrayList<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getFormattedValue(float value) {
        return labels.get(Math.round(value));
    }
}
