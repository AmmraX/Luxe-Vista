package com.example.luxevista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ServiceReservationsAdapter extends RecyclerView.Adapter<ServiceReservationsAdapter.ServiceReservationViewHolder> {

    private ArrayList<HashMap<String, String>> reservationsList;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat apiDateFormat;
    private SimpleDateFormat displayTimeFormat;
    private SimpleDateFormat apiTimeFormat;

    public ServiceReservationsAdapter(ArrayList<HashMap<String, String>> reservationsList, 
                                     SimpleDateFormat displayDateFormat, 
                                     SimpleDateFormat apiDateFormat) {
        this.reservationsList = reservationsList;
        this.displayDateFormat = displayDateFormat;
        this.apiDateFormat = apiDateFormat;
        this.displayTimeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        this.apiTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    }

    @NonNull
    @Override
    public ServiceReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_reservation, parent, false);
        return new ServiceReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceReservationViewHolder holder, int position) {
        HashMap<String, String> reservation = reservationsList.get(position);

        holder.tvServiceName.setText(reservation.get("service_name"));

        String serviceType = "Service";
        if (reservation.containsKey("category")) {
            String category = reservation.get("category");
            if ("spa".equalsIgnoreCase(category)) {
                serviceType = "Spa Service";
            } else if ("dining".equalsIgnoreCase(category)) {
                serviceType = "Dining Service";
            }
        }
        holder.tvServiceType.setText(serviceType);

        try {
            String dateStr = reservation.get("reservation_date");
            if (dateStr != null) {
                Date date = apiDateFormat.parse(dateStr);
                if (date != null) {
                    holder.tvReservationDate.setText(displayDateFormat.format(date));
                }
            }
        } catch (ParseException e) {
            holder.tvReservationDate.setText(reservation.get("reservation_date"));
        }

        try {
            String timeStr = reservation.get("reservation_time");
            if (timeStr != null) {
                Date time = apiTimeFormat.parse(timeStr);
                if (time != null) {
                    holder.tvReservationTime.setText(displayTimeFormat.format(time));
                }
            }
        } catch (ParseException e) {
            holder.tvReservationTime.setText(reservation.get("reservation_time"));
        }

        String status = reservation.get("status");
        holder.tvReservationStatus.setText(status != null ? status : "Pending");
        
        if ("confirmed".equalsIgnoreCase(status)) {
            holder.tvReservationStatus.setTextColor(0xFF4CAF50); // Green
        } else if ("cancelled".equalsIgnoreCase(status)) {
            holder.tvReservationStatus.setTextColor(0xFFF44336); // Red
        } else {
            holder.tvReservationStatus.setTextColor(0xFFFF9800); // Orange for pending
        }

        String price = reservation.get("price");
        holder.tvServicePrice.setText(price != null ? "$" + price : "N/A");
    }

    @Override
    public int getItemCount() {
        return reservationsList.size();
    }

    public static class ServiceReservationViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServiceType, tvReservationDate, tvReservationTime, tvReservationStatus, tvServicePrice;

        public ServiceReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvReservationDate = itemView.findViewById(R.id.tvReservationDate);
            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
            tvReservationStatus = itemView.findViewById(R.id.tvReservationStatus);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
        }
    }
} 