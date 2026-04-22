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

public class UserBookingsAdapter extends RecyclerView.Adapter<UserBookingsAdapter.BookingViewHolder> {

    private ArrayList<HashMap<String, String>> bookingsList;
    private SimpleDateFormat displayFormat, apiFormat;

    public UserBookingsAdapter(ArrayList<HashMap<String, String>> bookingsList, SimpleDateFormat displayFormat, SimpleDateFormat apiFormat) {
        this.bookingsList = bookingsList;
        this.displayFormat = displayFormat;
        this.apiFormat = apiFormat;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        HashMap<String, String> booking = bookingsList.get(position);
        
        holder.tvRoomType.setText(booking.get("room_type"));
        holder.tvRoomNumber.setText("Room " + booking.get("room_number"));
        
        try {
            Date checkInDate = apiFormat.parse(booking.get("check_in_date"));
            Date checkOutDate = apiFormat.parse(booking.get("check_out_date"));
            
            String formattedDateRange = displayFormat.format(checkInDate) + 
                                        " - " + 
                                        displayFormat.format(checkOutDate);
            holder.tvBookingDates.setText(formattedDateRange);
        } catch (ParseException e) {
            holder.tvBookingDates.setText(booking.get("check_in_date") + " - " + booking.get("check_out_date"));
        }
        
        holder.tvBookingPrice.setText("$" + booking.get("total_price"));
        
        String status = booking.get("status");
        holder.tvBookingStatus.setText(status);
        
        if ("confirmed".equalsIgnoreCase(status)) {
            holder.tvBookingStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("cancelled".equalsIgnoreCase(status)) {
            holder.tvBookingStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else if ("completed".equalsIgnoreCase(status)) {
            holder.tvBookingStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomType, tvRoomNumber, tvBookingDates, tvBookingPrice, tvBookingStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvBookingDates = itemView.findViewById(R.id.tvBookingDates);
            tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
        }
    }
} 