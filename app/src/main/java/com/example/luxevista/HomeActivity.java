package com.example.luxevista;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private Database database;
    private TextView tvWelcomeUser;
    private CardView cardRoomBooking, cardSpaService, cardDiningService, cardAttractionsService;
    private CardView cardMyBookings, cardPromotions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());
        
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }
        
        initializeViews();
        
        setWelcomeMessage();
        
        setupClickListeners();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void initializeViews() {
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        
        cardRoomBooking = findViewById(R.id.cardRoomBooking);
        cardSpaService = findViewById(R.id.cardSpaService);
        cardDiningService = findViewById(R.id.cardDiningService);
        cardAttractionsService = findViewById(R.id.cardAttractionsService);
        
        cardMyBookings = findViewById(R.id.cardMyBookings);
        cardPromotions = findViewById(R.id.cardPromotions);
    }
    
    private void setWelcomeMessage() {
        HashMap<String, String> user = sessionManager.getUserDetails();
        String username = user.get(SessionManager.KEY_USERNAME);
        
        tvWelcomeUser.setText("Welcome, " + username + "!");
    }
    
    private void setupClickListeners() {
        cardRoomBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RoomListActivity.class);
                startActivity(intent);
            }
        });
        
        cardSpaService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SpaServicesActivity.class);
                startActivity(intent);
            }
        });
        
        cardDiningService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DiningServicesActivity.class);
                startActivity(intent);
            }
        });
        
        cardAttractionsService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AttractionsActivity.class);
                startActivity(intent);
            }
        });
        
        cardMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserBookingsActivity.class);
                startActivity(intent);
            }
        });
        
        cardPromotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PromotionsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            sessionManager.logoutUser();
            
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
} 