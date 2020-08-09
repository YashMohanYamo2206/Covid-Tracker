package com.yash.delta_project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yash.delta_project.R;


import java.util.List;

public class indian_states_list_adapter extends RecyclerView.Adapter<indian_states_list_adapter.indian_states_list_ViewHolder> {

    private indian_states_list_adapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(indian_states_list_adapter.OnItemClickListener listener) {
        mListener = listener;
    }

    List<String> states;

    public indian_states_list_adapter(List<String> states) {
        this.states = states;
    }

    public class indian_states_list_ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public indian_states_list_ViewHolder(@NonNull View itemView) {
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
    public indian_states_list_adapter.indian_states_list_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.countries_item, parent, false);
        return new indian_states_list_adapter.indian_states_list_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull indian_states_list_adapter.indian_states_list_ViewHolder holder, int position) {
        String state = states.get(position);
        holder.textView.setText(state.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return states.size();
    }
}
