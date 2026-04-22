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

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder> {

    private final Context context;
    private final ArrayList<HashMap<String, String>> attractionsList;
    private final OnAttractionClickListener listener;

    public interface OnAttractionClickListener {
        void onMapButtonClick(HashMap<String, String> attraction);
    }

    public AttractionsAdapter(Context context, ArrayList<HashMap<String, String>> attractionsList, OnAttractionClickListener listener) {
        this.context = context;
        this.attractionsList = attractionsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        HashMap<String, String> attraction = attractionsList.get(position);

        holder.tvAttractionName.setText(attraction.get("name"));
        
        String category = attraction.get("category");
        if (category != null && !category.isEmpty()) {
            holder.tvAttractionCategory.setText(category);
        } else {
            holder.tvAttractionCategory.setText("Tourist Attraction");
        }
        
        holder.tvAttractionDescription.setText(attraction.get("description"));
        
        String distance = attraction.get("distance");
        if (distance != null) {
            try {
                double distanceValue = Double.parseDouble(distance);
                holder.tvAttractionDistance.setText(String.format("%.1f km", distanceValue));
            } catch (NumberFormatException e) {
                holder.tvAttractionDistance.setText(distance + " km");
            }
        } else {
            holder.tvAttractionDistance.setText("Distance not available");
        }
        
        String contactInfo = attraction.get("contact_info");
        if (contactInfo != null && !contactInfo.isEmpty()) {
            holder.tvAttractionContact.setText(contactInfo);
        } else {
            holder.tvAttractionContact.setText("No contact information available");
        }

        String imageUrl = attraction.get("image_url");
        holder.ivAttractionImage.setImageResource(R.drawable.image);

        holder.btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMapButtonClick(attraction);
                } else {
                    Toast.makeText(context, "View " + attraction.get("name") + " on map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionsList.size();
    }

    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAttractionImage;
        TextView tvAttractionName, tvAttractionCategory, tvAttractionDescription, 
                tvAttractionDistance, tvAttractionContact;
        Button btnViewOnMap;

        public AttractionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAttractionImage = itemView.findViewById(R.id.ivAttractionImage);
            tvAttractionName = itemView.findViewById(R.id.tvAttractionName);
            tvAttractionCategory = itemView.findViewById(R.id.tvAttractionCategory);
            tvAttractionDescription = itemView.findViewById(R.id.tvAttractionDescription);
            tvAttractionDistance = itemView.findViewById(R.id.tvAttractionDistance);
            tvAttractionContact = itemView.findViewById(R.id.tvAttractionContact);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
        }
    }
} 