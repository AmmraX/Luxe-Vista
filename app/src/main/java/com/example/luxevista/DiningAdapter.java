package com.example.luxevista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class DiningAdapter extends RecyclerView.Adapter<DiningAdapter.DiningViewHolder> {

    private final Context context;
    private final ArrayList<HashMap<String, String>> diningList;
    private final DiningReservationListener listener;

    public interface DiningReservationListener {
        void onDiningServiceReserved(HashMap<String, String> diningService);
    }

    public DiningAdapter(Context context, ArrayList<HashMap<String, String>> diningList, DiningReservationListener listener) {
        this.context = context;
        this.diningList = diningList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dining_service, parent, false);
        return new DiningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiningViewHolder holder, int position) {
        HashMap<String, String> diningService = diningList.get(position);

        holder.tvDiningName.setText(diningService.get("service_name"));
        holder.tvDiningDescription.setText(diningService.get("description"));
        
        String price = diningService.get("price");
        if (price != null) {
            holder.tvDiningPrice.setText("$" + price);
        } else {
            holder.tvDiningPrice.setText("Price not available");
        }

        String imageUrl = diningService.get("image_url");
        holder.ivDiningImage.setImageResource(R.drawable.image);

        holder.btnReserveDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDiningServiceReserved(diningService);
                } else {
                    Toast.makeText(context, "Reserving " + diningService.get("service_name"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return diningList.size();
    }

    public static class DiningViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDiningImage;
        TextView tvDiningName, tvDiningDescription, tvDiningPrice;
        Button btnReserveDining;

        public DiningViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDiningImage = itemView.findViewById(R.id.ivDiningImage);
            tvDiningName = itemView.findViewById(R.id.tvDiningName);
            tvDiningDescription = itemView.findViewById(R.id.tvDiningDescription);
            tvDiningPrice = itemView.findViewById(R.id.tvDiningPrice);
            btnReserveDining = itemView.findViewById(R.id.btnReserveDining);
        }
    }
} 