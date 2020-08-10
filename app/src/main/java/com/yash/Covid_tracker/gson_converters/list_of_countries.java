package com.yash.Covid_tracker.gson_converters;

import java.util.Comparator;
import java.util.List;

public class list_of_countries implements Comparator<country> {
    List<country> countries;

    public List<com.yash.Covid_tracker.gson_converters.country> getCountries() {
        return countries;
    }

    public void setCountries(List<com.yash.Covid_tracker.gson_converters.country> countries) {
        this.countries = countries;
    }


    @Override
    public int compare(country o1, country o2) {
        return o1.getCountry().compareTo(o2.getCountry());
    }


}
