package com.example.luxevista;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DiningServicesActivity extends AppCompatActivity implements DiningAdapter.DiningReservationListener {

    private static final String TAG = "DiningServicesActivity";
    private SessionManager sessionManager;
    private Database database;
    private RecyclerView recyclerDiningServices;
    private TextView tvNoDiningServices;
    private ProgressBar progressBar;
    private DiningAdapter diningAdapter;
    private ArrayList<HashMap<String, String>> diningList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dining_services);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fine Dining Services");

        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());

        recyclerDiningServices = findViewById(R.id.recyclerDiningServices);
        tvNoDiningServices = findViewById(R.id.tvNoDiningServices);
        progressBar = findViewById(R.id.progressBar);

        recyclerDiningServices.setLayoutManager(new LinearLayoutManager(this));
        diningList = new ArrayList<>();
        diningAdapter = new DiningAdapter(this, diningList, this);
        recyclerDiningServices.setAdapter(diningAdapter);

        addSampleDiningData();

        loadDiningServices();
    }

    private void addSampleDiningData() {
        // Get current services to check if we need to add sample data
        ArrayList<HashMap<String, String>> existingServices = database.getServicesByCategory("dining");

        if (existingServices.isEmpty()) {
            database.addService(
                    "Le Gourmet Restaurant",
                    "dining",
                    150,
                    "Experience exquisite French cuisine prepared by our award-winning chefs. Perfect for a romantic dinner or special occasion.",
                    120,
                    "le_gourmet_restaurant.jpg"
            );

            database.addService(
                    "Ocean View Seafood",
                    "dining",
                    95.00,
                    "Fresh seafood caught daily, served with breathtaking views of the ocean. Enjoy our chef's special preparations with local ingredients.",
                    90,
                    "ocean_view_seafood.jpg"
            );

            database.addService(
                    "Asian Fusion Bistro",
                    "dining",
                    85.00,
                    "Modern Asian cuisine with a Western twist. Our menu features innovative dishes that combine traditional Asian flavors with contemporary techniques.",
                    80,
                    "asian_fusion_bistro.jpg"
            );

            database.addService(
                    "Italian Trattoria",
                    "dining",
                    70.00,
                    "Authentic Italian recipes passed down through generations. Our pasta is made fresh daily, and our pizzas are baked in a traditional wood-fired oven.",
                    75,
                    "italian_trattoria.jpg"
            );

            database.addService(
                    "Rooftop BBQ & Grill",
                    "dining",
                    65.00,
                    "Enjoy perfectly grilled steaks and BBQ specialties on our scenic rooftop terrace. Our meat is sourced from local farms for the best quality and flavor.",
                    100,
                    "rooftop_bbq_grill.jpg"
            );

            Toast.makeText(this, "Sample dining services added", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDiningServices() {
        showLoading(true);

        try {
            ArrayList<HashMap<String, String>> services = database.getServicesByCategory("dining");
            
            diningList.clear();
            diningList.addAll(services);
            diningAdapter.notifyDataSetChanged();

            if (diningList.isEmpty()) {
                tvNoDiningServices.setVisibility(View.VISIBLE);
            } else {
                tvNoDiningServices.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading dining services: " + e.getMessage());
            Toast.makeText(this, "Error loading dining services: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            tvNoDiningServices.setVisibility(View.VISIBLE);
        } finally {
            showLoading(false);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerDiningServices.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (isLoading) {
            tvNoDiningServices.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDiningServiceReserved(HashMap<String, String> diningService) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                DiningServicesActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                        String date = dateFormat.format(selectedDate.getTime());
                                        
                                        String time = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                                        
                                        int userId = sessionManager.getUserId();
                                        int serviceId = Integer.parseInt(diningService.get("id"));
                                        
                                        try {
                                            long reservationId = database.reserveService(userId, serviceId, date, time);
                                            if (reservationId > 0) {
                                                Toast.makeText(DiningServicesActivity.this, 
                                                        "Reservation for " + diningService.get("service_name") + " confirmed!", 
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(DiningServicesActivity.this, 
                                                        "Failed to make reservation", 
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(DiningServicesActivity.this, 
                                                    "Error making reservation: " + e.getMessage(), 
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 