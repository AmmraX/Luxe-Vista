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

public class SpaServicesActivity extends AppCompatActivity implements SpaAdapter.SpaBookingListener {

    private static final String TAG = "SpaServicesActivity";
    private SessionManager sessionManager;
    private Database database;
    private RecyclerView recyclerSpaServices;
    private TextView tvNoSpaServices;
    private ProgressBar progressBar;
    private SpaAdapter spaAdapter;
    private ArrayList<HashMap<String, String>> spaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa_services);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Spa & Wellness Services");

        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());

        recyclerSpaServices = findViewById(R.id.recyclerSpaServices);
        tvNoSpaServices = findViewById(R.id.tvNoSpaServices);
        progressBar = findViewById(R.id.progressBar);

        recyclerSpaServices.setLayoutManager(new LinearLayoutManager(this));
        spaList = new ArrayList<>();
        spaAdapter = new SpaAdapter(this, spaList, this);
        recyclerSpaServices.setAdapter(spaAdapter);

        addSampleSpaData();

        loadSpaServices();
    }

    private void addSampleSpaData() {
        ArrayList<HashMap<String, String>> existingServices = database.getServicesByCategory("spa");

        if (existingServices.isEmpty()) {
            database.addService(
                    "Swedish Massage",
                    "spa",
                    85.00,
                    "A classic relaxation massage using long, flowing strokes to promote relaxation, ease muscle tension and create a sense of wellbeing.",
                    60,
                    "@drawable/swedish_massage.jpg"
            );

            database.addService(
                    "Deep Tissue Massage",
                    "spa",
                    110.00,
                    "A therapeutic massage focusing on realigning deeper layers of muscles. It is used for chronic aches and pain and contracted areas.",
                    90,
                    "deep_tissue_massage.jpg"
            );

            database.addService(
                    "Hot Stone Therapy",
                    "spa",
                    125.00,
                    "Smooth, heated stones are placed on key points of the body. The massage therapist may also hold stones and apply gentle pressure with them.",
                    75,
                    "hot_stone_therapy.jpg"
            );

            database.addService(
                    "Aromatherapy Facial",
                    "spa",
                    95.00,
                    "A luxurious facial treatment using essential oils that are selected to address specific skin concerns, promoting a clear, well-balanced complexion.",
                    60,
                    "aromatherapy_facial.jpg"
            );

            database.addService(
                    "Detox Body Wrap",
                    "spa",
                    135.00,
                    "A purifying treatment that helps rid the body of toxins and excess water, while nourishing the skin with essential minerals and nutrients.",
                    90,
                    "detox_body_wrap.jpg"
            );

            Toast.makeText(this, "Sample spa services added", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSpaServices() {
        showLoading(true);

        try {
            ArrayList<HashMap<String, String>> services = database.getServicesByCategory("spa");
            
            spaList.clear();
            spaList.addAll(services);
            spaAdapter.notifyDataSetChanged();

            if (spaList.isEmpty()) {
                tvNoSpaServices.setVisibility(View.VISIBLE);
            } else {
                tvNoSpaServices.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading spa services: " + e.getMessage());
            Toast.makeText(this, "Error loading spa services: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            tvNoSpaServices.setVisibility(View.VISIBLE);
        } finally {
            showLoading(false);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerSpaServices.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (isLoading) {
            tvNoSpaServices.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSpaServiceBooked(HashMap<String, String> spaService) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                SpaServicesActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                        String date = dateFormat.format(selectedDate.getTime());
                                        
                                        String time = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
                                        
                                        int userId = sessionManager.getUserId();
                                        int serviceId = Integer.parseInt(spaService.get("id"));
                                        
                                        try {
                                            long reservationId = database.reserveService(userId, serviceId, date, time);
                                            if (reservationId > 0) {
                                                Toast.makeText(SpaServicesActivity.this, 
                                                        "Appointment for " + spaService.get("service_name") + " confirmed!", 
                                                        Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(SpaServicesActivity.this, 
                                                        "Failed to book appointment", 
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            Toast.makeText(SpaServicesActivity.this, 
                                                    "Error booking appointment: " + e.getMessage(), 
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