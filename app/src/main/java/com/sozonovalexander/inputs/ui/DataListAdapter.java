package com.sozonovalexander.inputs.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sozonovalexander.inputs.R;

import java.util.ArrayList;
import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.DataListViewHolder> {
    private List<String> localDataSet;
    private final OnItemClickListener clickListener;

    public DataListAdapter(ArrayList<String> localDataSet, OnItemClickListener clickListener) {
        this.localDataSet = localDataSet;
        this.clickListener = clickListener;
    }

    public void updateDataSet(List<String> dataSet) {
        localDataSet = dataSet;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DataListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list_view_holder_layout, parent, false);
        return new DataListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataListViewHolder holder, int position) {
        holder.getTextView().setText(localDataSet.get(position));
        holder.getCardView().setOnClickListener(view -> {
            clickListener.onClick(localDataSet.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class DataListViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final CardView cardView;

        public DataListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.dataListText);
            cardView = (CardView) itemView.findViewById(R.id.dataListCard);
        }

        public TextView getTextView() {
            return textView;
        }

        public CardView getCardView() {
            return cardView;
        }
    }

    public interface OnItemClickListener {
        void onClick(String value);
    }
}
