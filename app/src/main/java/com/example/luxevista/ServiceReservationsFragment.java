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

public class ServiceReservationsFragment extends Fragment {

    private SessionManager sessionManager;
    private Database database;
    private RecyclerView recyclerServiceReservations;
    private TextView tvNoServiceReservations;
    private ProgressBar progressBarServiceReservations;
    private ServiceReservationsAdapter reservationsAdapter;
    private ArrayList<HashMap<String, String>> reservationsList;
    
    private SimpleDateFormat apiFormat, displayFormat;

    public ServiceReservationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        
        sessionManager = new SessionManager(requireContext());
        database = new Database(requireContext());
        
        recyclerServiceReservations = view.findViewById(R.id.recyclerServiceReservations);
        tvNoServiceReservations = view.findViewById(R.id.tvNoServiceReservations);
        progressBarServiceReservations = view.findViewById(R.id.progressBarServiceReservations);
        
        recyclerServiceReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        reservationsList = new ArrayList<>();
        reservationsAdapter = new ServiceReservationsAdapter(reservationsList, displayFormat, apiFormat);
        recyclerServiceReservations.setAdapter(reservationsAdapter);
        
        loadServiceReservations();
    }

    public void loadServiceReservations() {
        showLoading(true);
        
        try {
            int userId = sessionManager.getUserId();
            reservationsList.clear();
            reservationsList.addAll(database.getUserServiceReservations(userId));
            reservationsAdapter.notifyDataSetChanged();
            
            if (reservationsList.isEmpty()) {
                tvNoServiceReservations.setVisibility(View.VISIBLE);
            } else {
                tvNoServiceReservations.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error loading service reservations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            showLoading(false);
        }
    }
    
    private void showLoading(boolean isLoading) {
        progressBarServiceReservations.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerServiceReservations.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }
} 