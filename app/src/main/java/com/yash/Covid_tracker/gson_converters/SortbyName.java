package com.yash.Covid_tracker.gson_converters;

import java.util.Comparator;

public class SortbyName implements Comparator<country> {
    @Override
    public int compare(country o1, country o2) {
        return o1.getCountry().compareTo(o2.getCountry());
    }
}



