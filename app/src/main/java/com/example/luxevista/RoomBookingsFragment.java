package com.example.luxevista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class RoomBookingsFragment extends Fragment {

    private SessionManager sessionManager;
    private Database database;
    private RecyclerView recyclerRoomBookings;
    private TextView tvNoRoomBookings;
    private ProgressBar progressBarRoomBookings;
    private UserBookingsAdapter bookingsAdapter;
    private ArrayList<HashMap<String, String>> bookingsList;
    
    private SimpleDateFormat apiFormat, displayFormat;

    public RoomBookingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        
        sessionManager = new SessionManager(requireContext());
        database = new Database(requireContext());
        
        recyclerRoomBookings = view.findViewById(R.id.recyclerRoomBookings);
        tvNoRoomBookings = view.findViewById(R.id.tvNoRoomBookings);
        progressBarRoomBookings = view.findViewById(R.id.progressBarRoomBookings);
        
        recyclerRoomBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        bookingsList = new ArrayList<>();
        bookingsAdapter = new UserBookingsAdapter(bookingsList, displayFormat, apiFormat);
        recyclerRoomBookings.setAdapter(bookingsAdapter);
        
        loadRoomBookings();
    }

    public void loadRoomBookings() {
        showLoading(true);
        
        try {
            int userId = sessionManager.getUserId();
            bookingsList.clear();
            bookingsList.addAll(database.getUserBookings(userId));
            bookingsAdapter.notifyDataSetChanged();
            
            if (bookingsList.isEmpty()) {
                tvNoRoomBookings.setVisibility(View.VISIBLE);
            } else {
                tvNoRoomBookings.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading room bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            showLoading(false);
        }
    }
    
    private void showLoading(boolean isLoading) {
        progressBarRoomBookings.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerRoomBookings.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
} 