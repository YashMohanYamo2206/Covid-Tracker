package com.yash.Covid_tracker.DataBase;

import android.provider.BaseColumns;

public class pinned_indian_states_contract {
    public class pinned_indian_states_entry implements BaseColumns {
        public static final String TABLE_NAME = "pinned_indian_states";
        public static final String COLUMN_STATE_NAME = "state_name";
        public static final String COLUMN_POSITION = "position";
        public static final String COLUMN_TIMESTAMP = "timeStamp";
    }
}
