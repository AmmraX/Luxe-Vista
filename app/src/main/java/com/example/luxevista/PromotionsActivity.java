package com.example.luxevista;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PromotionsActivity extends AppCompatActivity {

    private static final String TAG = "PromotionsActivity";
    private SessionManager sessionManager;
    private Database database;
    private RecyclerView recyclerPromotions;
    private TextView tvNoPromotions;
    private ProgressBar progressBar;
    private PromotionsAdapter promotionsAdapter;
    private ArrayList<HashMap<String, String>> promotionsList;
    
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        
        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());
        
        recyclerPromotions = findViewById(R.id.recyclerPromotions);
        tvNoPromotions = findViewById(R.id.tvNoPromotions);
        progressBar = findViewById(R.id.progressBar);
        
        recyclerPromotions.setLayoutManager(new LinearLayoutManager(this));
        promotionsList = new ArrayList<>();
        promotionsAdapter = new PromotionsAdapter(this, promotionsList, dateFormat);
        recyclerPromotions.setAdapter(promotionsAdapter);
        
        addSamplePromotionsData();
        
        loadPromotions();
    }
    
    private void addSamplePromotionsData() {
        try {
            Log.d(TAG, "Adding sample promotion data");
            

            database.addPromotion(
                "Summer Escape Package", 
                "Book a 3-night stay and get 15% off the entire booking. Perfect for a summer getaway!",
                null, 15.0, 
                "2023-06-01", "2023-08-31", 
                "SUMMER15"
            );
            
            database.addPromotion(
                "Honeymoon Special", 
                "Complimentary champagne, chocolate-covered strawberries, and spa treatments for newlyweds.",
                50.0, null, 
                "2023-01-01", "2023-12-31", 
                "HONEYMOON"
            );
            
            database.addPromotion(
                "Weekend Adventure", 
                "20% discount on all bookings for weekend stays. Includes free access to all adventure activities.",
                null, 20.0, 
                "2023-09-01", "2023-11-30", 
                "WEEKEND20"
            );
            
            database.addPromotion(
                "Luxury Dining Experience", 
                "Book any suite for 2+ nights and receive a $100 dining credit at our 5-star restaurant.",
                100.0, null, 
                "2023-10-01", "2023-12-15", 
                "DINE100"
            );
            
            database.addPromotion(
                "Holiday Season Special", 
                "Book your holiday vacation early and receive 25% off plus complimentary airport transfers.",
                null, 25.0, 
                "2023-12-01", "2024-01-15", 
                "HOLIDAY25"
            );
            
        } catch (Exception e) {
            Log.e(TAG, "Error adding sample data: " + e.getMessage());
        }
    }
    
    private void loadPromotions() {
        showLoading(true);
        
        try {
            ArrayList<HashMap<String, String>> currentPromotions = database.getActivePromotions();
            Log.d(TAG, "Loaded " + currentPromotions.size() + " promotions");
            
            promotionsList.clear();
            promotionsList.addAll(currentPromotions);
            promotionsAdapter.notifyDataSetChanged();
            
            if (promotionsList.isEmpty()) {
                tvNoPromotions.setVisibility(View.VISIBLE);
                Toast.makeText(this, "No active promotions found", Toast.LENGTH_SHORT).show();
            } else {
                tvNoPromotions.setVisibility(View.GONE);
                Toast.makeText(this, "Found " + promotionsList.size() + " promotions", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading promotions: " + e.getMessage());
            Toast.makeText(this, "Error loading promotions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            tvNoPromotions.setVisibility(View.VISIBLE);
        } finally {
            showLoading(false);
        }
    }
    
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerPromotions.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (isLoading) {
            tvNoPromotions.setVisibility(View.GONE);
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