package com.example.luxevista;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.PromotionViewHolder> {

    private static final String TAG = "PromotionsAdapter";
    private Context context;
    private ArrayList<HashMap<String, String>> promotionsList;
    private SimpleDateFormat displayFormat;
    private SimpleDateFormat apiFormat;

    public PromotionsAdapter(Context context, ArrayList<HashMap<String, String>> promotionsList, 
                          SimpleDateFormat displayFormat) {
        this.context = context;
        this.promotionsList = promotionsList;
        this.displayFormat = displayFormat;
        this.apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        Log.d(TAG, "Binding promotion at position: " + position);
        
        HashMap<String, String> promotion = promotionsList.get(position);
        if (promotion == null) {
            Log.e(TAG, "Promotion is null at position: " + position);
            return;
        }
        
        for (String key : promotion.keySet()) {
            Log.d(TAG, "Promotion data: " + key + " = " + promotion.get(key));
        }
        
        String title = promotion.get("title");
        holder.tvPromotionTitle.setText(title != null ? title : "Promotion");
        
        String description = promotion.get("description");
        holder.tvPromotionDescription.setText(description != null ? description : "Special offer for our guests");
        
        try {
            String validUntilStr = promotion.get("valid_until");
            if (validUntilStr != null) {
                Date validUntil = apiFormat.parse(validUntilStr);
                String validDates = "Valid until: " + displayFormat.format(validUntil);
                holder.tvPromotionValidity.setText(validDates);
            } else {
                holder.tvPromotionValidity.setText("Limited time offer");
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            // Fallback if date parsing fails
            holder.tvPromotionValidity.setText("Limited time offer");
        }
        
        String discountAmount = promotion.get("discount_amount");
        String discountPercentage = promotion.get("discount_percentage");
        
        try {
            if (discountAmount != null && !discountAmount.equals("null") && !discountAmount.isEmpty()) {
                double amount = Double.parseDouble(discountAmount);
                holder.tvPromotionDiscount.setText("$" + String.format(Locale.US, "%.2f", amount) + " OFF");
            } else if (discountPercentage != null && !discountPercentage.equals("null") && !discountPercentage.isEmpty()) {
                double percentage = Double.parseDouble(discountPercentage);
                holder.tvPromotionDiscount.setText(String.format(Locale.US, "%.0f%%", percentage) + " OFF");
            } else {
                holder.tvPromotionDiscount.setText("Special Offer");
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing discount: " + e.getMessage());
            holder.tvPromotionDiscount.setText("Special Offer");
        }
        
        String promoCode = promotion.get("promo_code");
        holder.tvPromoCode.setText("Code: " + (promoCode != null ? promoCode : "SPECIAL"));
        
        holder.btnUsePromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // In a real app, this would navigate to booking with the promo code applied
                Toast.makeText(context, 
                        "Promo code " + promotion.get("promo_code") + " copied! Use it during booking.", 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return promotionsList != null ? promotionsList.size() : 0;
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromotionTitle, tvPromotionDescription, tvPromotionValidity, 
                 tvPromotionDiscount, tvPromoCode;
        Button btnUsePromotion;

        public PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPromotionTitle = itemView.findViewById(R.id.tvPromotionTitle);
            tvPromotionDescription = itemView.findViewById(R.id.tvPromotionDescription);
            tvPromotionValidity = itemView.findViewById(R.id.tvPromotionValidity);
            tvPromotionDiscount = itemView.findViewById(R.id.tvPromotionDiscount);
            tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
            btnUsePromotion = itemView.findViewById(R.id.btnUsePromotion);
        }
    }
} 