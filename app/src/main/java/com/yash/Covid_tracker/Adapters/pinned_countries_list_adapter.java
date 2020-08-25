package com.yash.Covid_tracker.Adapters;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yash.Covid_tracker.DataBase.pinned_countries_contract;
import com.yash.Covid_tracker.R;

public class pinned_countries_list_adapter extends RecyclerView.Adapter<pinned_countries_list_adapter.pinned_list_viewHolder> {

    private Cursor mCursor;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemCLick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public pinned_countries_list_adapter(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    public class pinned_list_viewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public pinned_list_viewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.country_name);
            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemCLick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public pinned_list_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.countries_item, parent, false);
        return new pinned_list_viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pinned_list_viewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String p1_name = mCursor.getString(mCursor.getColumnIndex(pinned_countries_contract.pinned_countries_entry.COLUMN_COUNTRY_NAME)).toUpperCase();
        holder.mTextView.setText(p1_name);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

}
