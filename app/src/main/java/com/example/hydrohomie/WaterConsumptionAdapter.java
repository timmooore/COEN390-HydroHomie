package com.example.hydrohomie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hydrohomie.WaterConsumption;
import com.example.hydrohomie.R;
import java.util.List;

public class WaterConsumptionAdapter extends RecyclerView.Adapter<WaterConsumptionAdapter.ViewHolder> {
    private List<WaterConsumption> waterConsumptionList;

    public WaterConsumptionAdapter(List<WaterConsumption> waterConsumptionList) {
        this.waterConsumptionList = waterConsumptionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_consumption, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterConsumption waterConsumption = waterConsumptionList.get(position);
        holder.dateTextView.setText(String.format("%04d-%02d-%02d", waterConsumption.getYear(), waterConsumption.getMonth(), waterConsumption.getDay()));
        holder.amountTextView.setText(String.valueOf(waterConsumption.getAmount()));
    }

    @Override
    public int getItemCount() {
        return waterConsumptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView amountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }
}
