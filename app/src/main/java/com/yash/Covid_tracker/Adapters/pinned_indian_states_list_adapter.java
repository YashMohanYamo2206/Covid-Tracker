package com.yash.Covid_tracker.Adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yash.Covid_tracker.DataBase.pinned_countries_contract;
import com.yash.Covid_tracker.DataBase.pinned_indian_states_contract;
import com.yash.Covid_tracker.R;

public class pinned_indian_states_list_adapter extends RecyclerView.Adapter<pinned_indian_states_list_adapter.pinned_indian_states_list_viewHolder> {

    private Cursor mCursor;

    public pinned_indian_states_list_adapter(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onStatesItemCLick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public class pinned_indian_states_list_viewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public pinned_indian_states_list_viewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.country_name);
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onStatesItemCLick(position);
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public pinned_indian_states_list_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.states_item, parent, false);
        return new pinned_indian_states_list_viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pinned_indian_states_list_viewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String p1_name = mCursor.getString(mCursor.getColumnIndex(pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME));
        holder.mTextView.setText(p1_name);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
