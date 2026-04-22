package com.example.luxevista;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RoomListActivity extends AppCompatActivity implements RoomAdapter.RoomBookListener {

    private SessionManager sessionManager;
    private Database database;
    private TextView tvCheckInDate, tvCheckOutDate, tvEmptyState;
    private Button btnSearchRooms;
    private RecyclerView recyclerRooms;
    private ProgressBar progressBar;
    private RoomAdapter roomAdapter;
    private ArrayList<HashMap<String, String>> roomsList;

    private Calendar checkInCalendar, checkOutCalendar;
    private SimpleDateFormat dateFormat, apiDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        btnSearchRooms = findViewById(R.id.btnSearchRooms);
        recyclerRooms = findViewById(R.id.recyclerRooms);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);

        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        
        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1); // Default checkout next day
        
        updateDateDisplay();

        recyclerRooms.setLayoutManager(new LinearLayoutManager(this));
        roomsList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomsList, this);
        recyclerRooms.setAdapter(roomAdapter);

        tvCheckInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        tvCheckOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);
            }
        });

        btnSearchRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAvailableRooms();
            }
        });

        addSampleRoomsIfNeeded();
        
        searchAvailableRooms();
    }

    private void addSampleRoomsIfNeeded() {
        ArrayList<HashMap<String, String>> existingRooms = database.getAllRooms();
        
        if (existingRooms.isEmpty()) {
            database.addRoom("101", "Deluxe Ocean View", 259.99, 2,
                    "Spacious room with breathtaking ocean views and luxury amenities", 
                    "King bed, Ocean view, Balcony, Mini-bar, Free Wi-Fi, 55\" TV", 
                    "room_image_101.jpg");
            
            database.addRoom("102", "Standard Double", 189.99, 2, 
                    "Comfortable room with all essential amenities for a pleasant stay", 
                    "Queen bed, Garden view, Free Wi-Fi, 42\" TV", 
                    "room_image_102.jpg");
            
            database.addRoom("201", "Executive Suite", 359.99, 4, 
                    "Luxurious suite with separate living area and premium facilities", 
                    "King bed, Ocean view, Balcony, Living room, Jacuzzi, Free Wi-Fi, 65\" TV", 
                    "room_image_201.jpg");
            
            database.addRoom("202", "Family Room", 299.99, 4, 
                    "Spacious room designed for families with extra space and amenities", 
                    "2 Queen beds, Pool view, Free Wi-Fi, Mini-fridge, 50\" TV", 
                    "room_image_202.jpg");
            
            database.addRoom("301", "Honeymoon Suite", 459.99, 2, 
                    "Romantic suite with special touches perfect for couples", 
                    "King bed, Ocean view, Private balcony, Champagne service, Jacuzzi, Free Wi-Fi", 
                    "room_image_301.jpg");

            database.addRoom("302", "Penthouse Suite", 599.99, 6, 
                    "Our most luxurious accommodation with panoramic views and exclusive amenities", 
                    "2 King beds, 360° views, Private terrace, Full kitchen, Living room, Dining area, Free Wi-Fi", 
                    "room_image_302.jpg");
                    
            Toast.makeText(this, "Sample room data added", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog(final boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (isCheckIn) {
                            checkInCalendar = selectedCalendar;
                            if (checkInCalendar.after(checkOutCalendar)) {
                                // If check-in is after check-out, set check-out to the day after check-in
                                checkOutCalendar = (Calendar) checkInCalendar.clone();
                                checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            }
                        } else {
                            if (selectedCalendar.after(checkInCalendar)) {
                                checkOutCalendar = selectedCalendar;
                            } else {
                                Toast.makeText(RoomListActivity.this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
                            }
                        }
                        updateDateDisplay();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        Calendar today = Calendar.getInstance();
        if (isCheckIn) {
            datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
        } else {
            Calendar minCheckout = (Calendar) checkInCalendar.clone();
            minCheckout.add(Calendar.DAY_OF_MONTH, 1);
            datePickerDialog.getDatePicker().setMinDate(minCheckout.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        tvCheckInDate.setText(dateFormat.format(checkInCalendar.getTime()));
        tvCheckOutDate.setText(dateFormat.format(checkOutCalendar.getTime()));
    }

    private void searchAvailableRooms() {
        showLoading(true);
        try {
            String checkInDate = apiDateFormat.format(checkInCalendar.getTime());
            String checkOutDate = apiDateFormat.format(checkOutCalendar.getTime());

            roomsList.clear();
            roomsList.addAll(database.getAvailableRooms(checkInDate, checkOutDate));
            roomAdapter.notifyDataSetChanged();

            if (roomsList.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading rooms: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            showLoading(false);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerRooms.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RoomListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRoomBooked(HashMap<String, String> room) {
        try {
            String checkInDateStr = apiDateFormat.format(checkInCalendar.getTime());
            String checkOutDateStr = apiDateFormat.format(checkOutCalendar.getTime());

            Date checkInDate = apiDateFormat.parse(checkInDateStr);
            Date checkOutDate = apiDateFormat.parse(checkOutDateStr);
            long diffInMillies = Math.abs(checkOutDate.getTime() - checkInDate.getTime());
            long nights = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            double pricePerNight = Double.parseDouble(room.get("price"));
            double totalPrice = pricePerNight * nights;

            Intent intent = new Intent(RoomListActivity.this, RoomBookingActivity.class);
            intent.putExtra("roomId", room.get("id"));
            intent.putExtra("roomNumber", room.get("room_number"));
            intent.putExtra("roomType", room.get("room_type"));
            intent.putExtra("roomPrice", room.get("price"));
            intent.putExtra("roomFeatures", room.get("features"));
            intent.putExtra("checkInDate", checkInDateStr);
            intent.putExtra("checkOutDate", checkOutDateStr);
            intent.putExtra("nights", nights);
            intent.putExtra("totalPrice", totalPrice);
            startActivity(intent);

        } catch (ParseException e) {
            Toast.makeText(this, "Error processing dates: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 