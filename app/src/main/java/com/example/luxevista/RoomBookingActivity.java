package com.example.luxevista;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RoomBookingActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private Database database;
    
    private ImageView imgRoom;
    private TextView tvRoomType, tvRoomNumber, tvRoomPrice, tvRoomFeatures;
    private TextView tvCheckInDate, tvCheckOutDate, tvNumberOfNights, tvTotalPrice;
    private Button btnConfirmBooking, btnCancel;
    
    private int userId, roomId;
    private String checkInDate, checkOutDate;
    private long nights;
    private double totalPrice;
    private SimpleDateFormat displayFormat, apiFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());
        
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }
        
        userId = sessionManager.getUserId();
        
        displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        
        imgRoom = findViewById(R.id.imgRoom);
        tvRoomType = findViewById(R.id.tvRoomType);
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        tvRoomFeatures = findViewById(R.id.tvRoomFeatures);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvNumberOfNights = findViewById(R.id.tvNumberOfNights);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnCancel = findViewById(R.id.btnCancel);
        
        if (getIntent().hasExtra("roomId")) {
            roomId = Integer.parseInt(getIntent().getStringExtra("roomId"));
            String roomNumber = getIntent().getStringExtra("roomNumber");
            String roomType = getIntent().getStringExtra("roomType");
            String roomPrice = getIntent().getStringExtra("roomPrice");
            String roomFeatures = getIntent().getStringExtra("roomFeatures");
            checkInDate = getIntent().getStringExtra("checkInDate");
            checkOutDate = getIntent().getStringExtra("checkOutDate");
            nights = getIntent().getLongExtra("nights", 1);
            totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            
            tvRoomType.setText(roomType);
            tvRoomNumber.setText("Room " + roomNumber);
            tvRoomPrice.setText(currencyFormat.format(Double.parseDouble(roomPrice)) + " per night");
            tvRoomFeatures.setText(roomFeatures);
            
            try {
                Date checkIn = apiFormat.parse(checkInDate);
                Date checkOut = apiFormat.parse(checkOutDate);
                tvCheckInDate.setText(displayFormat.format(checkIn));
                tvCheckOutDate.setText(displayFormat.format(checkOut));
            } catch (ParseException e) {
                tvCheckInDate.setText(checkInDate);
                tvCheckOutDate.setText(checkOutDate);
            }
            
            tvNumberOfNights.setText(nights + (nights > 1 ? " nights" : " night"));
            tvTotalPrice.setText(currencyFormat.format(totalPrice));
        } else {
            Toast.makeText(this, "Error loading room details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBooking();
            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void confirmBooking() {
        btnConfirmBooking.setEnabled(false);
        
        long bookingId = database.bookRoom(userId, roomId, checkInDate, checkOutDate, totalPrice);
        
        if (bookingId > 0) {
            addSampleBookingsIfNeeded();
            
            Toast.makeText(this, getString(R.string.booking_successful), Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.booking_failed), Toast.LENGTH_LONG).show();
            btnConfirmBooking.setEnabled(true);
        }
    }
    
    private void addSampleBookingsIfNeeded() {
        try {
            int userId = sessionManager.getUserId();
            if (database.getUserBookings(userId).size() <= 1) { // If this is the first real booking
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                
                String pastCheckIn1 = "2023-05-10";
                String pastCheckOut1 = "2023-05-15";
                database.bookRoom(userId, 2, pastCheckIn1, pastCheckOut1, 949.95);
                
                String pastCheckIn2 = "2023-06-22";
                String pastCheckOut2 = "2023-06-25";
                database.bookRoom(userId, 3, pastCheckIn2, pastCheckOut2, 1079.97);
                
                String futureCheckIn = "2023-12-24";
                String futureCheckOut = "2023-12-31";
                database.bookRoom(userId, 6, futureCheckIn, futureCheckOut, 4199.93);
            }
        } catch (Exception e) {
        }
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