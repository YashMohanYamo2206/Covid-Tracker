package com.yash.delta_project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yash.delta_project.R;
import com.yash.delta_project.gson_converters.*;

import java.util.List;

public class countries_list_adapter extends RecyclerView.Adapter<countries_list_adapter.countries_list_ViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    List<country> countries;

    public countries_list_adapter(List<country> countries) {
        this.countries = countries;
    }

    public class countries_list_ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public countries_list_ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.country_name);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public countries_list_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.countries_item, parent, false);
        return new countries_list_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull countries_list_ViewHolder holder, int position) {
        country country = countries.get(position);
        holder.textView.setText(country.getCountry().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }
}
