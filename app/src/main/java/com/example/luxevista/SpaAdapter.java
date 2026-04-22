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

public class SpaAdapter extends RecyclerView.Adapter<SpaAdapter.SpaViewHolder> {

    private final Context context;
    private final ArrayList<HashMap<String, String>> spaList;
    private final SpaBookingListener listener;

    public interface SpaBookingListener {
        void onSpaServiceBooked(HashMap<String, String> spaService);
    }

    public SpaAdapter(Context context, ArrayList<HashMap<String, String>> spaList, SpaBookingListener listener) {
        this.context = context;
        this.spaList = spaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spa_service, parent, false);
        return new SpaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpaViewHolder holder, int position) {
        HashMap<String, String> spaService = spaList.get(position);

        holder.tvSpaName.setText(spaService.get("service_name"));
        holder.tvSpaDescription.setText(spaService.get("description"));
        
        String price = spaService.get("price");
        if (price != null) {
            holder.tvSpaPrice.setText("$" + price);
        } else {
            holder.tvSpaPrice.setText("Price not available");
        }
        
        String duration = spaService.get("duration");
        if (duration != null) {
            holder.tvSpaDuration.setText(duration + " min");
        } else {
            holder.tvSpaDuration.setText("Duration not specified");
        }

        String imageUrl = spaService.get("image_url");
        holder.ivSpaImage.setImageResource(R.drawable.image);

        holder.btnBookSpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSpaServiceBooked(spaService);
                } else {
                    Toast.makeText(context, "Booking " + spaService.get("service_name"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return spaList.size();
    }

    public static class SpaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSpaImage;
        TextView tvSpaName, tvSpaDescription, tvSpaPrice, tvSpaDuration;
        Button btnBookSpa;

        public SpaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSpaImage = itemView.findViewById(R.id.ivSpaImage);
            tvSpaName = itemView.findViewById(R.id.tvSpaName);
            tvSpaDescription = itemView.findViewById(R.id.tvSpaDescription);
            tvSpaPrice = itemView.findViewById(R.id.tvSpaPrice);
            tvSpaDuration = itemView.findViewById(R.id.tvSpaDuration);
            btnBookSpa = itemView.findViewById(R.id.btnBookSpa);
        }
    }
} 