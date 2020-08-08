package com.yash.delta_project.axis_formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class X_axis_formatter__line_chart extends ValueFormatter {

    List<String> labels = new ArrayList<>();

    public X_axis_formatter__line_chart(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getFormattedValue(float value) {
        return labels.get(Math.round(value));
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return getFormattedValue(value);
    }
}
