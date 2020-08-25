package com.yash.Covid_tracker.DataBase;

import android.provider.BaseColumns;

public class pinned_countries_contract {

    public class pinned_countries_entry implements BaseColumns {
        public static final String TABLE_NAME = "pinned_countries";
        public static final String COLUMN_COUNTRY_NAME = "country_name";
        public static final String COLUMN_IMAGE_URL = "country_flag";
        public static final String COLUMN_SLUG = "slug";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
