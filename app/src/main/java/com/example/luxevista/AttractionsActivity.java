package com.example.luxevista;

import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AttractionsActivity extends AppCompatActivity implements AttractionsAdapter.OnAttractionClickListener {

    private static final String TAG = "AttractionsActivity";
    private SessionManager sessionManager;
    private Database database;
    private RecyclerView recyclerAttractions;
    private TextView tvNoAttractions;
    private ProgressBar progressBar;
    private AttractionsAdapter attractionsAdapter;
    private ArrayList<HashMap<String, String>> attractionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Local Attractions");

        sessionManager = new SessionManager(getApplicationContext());
        database = new Database(getApplicationContext());

        recyclerAttractions = findViewById(R.id.recyclerAttractions);
        tvNoAttractions = findViewById(R.id.tvNoAttractions);
        progressBar = findViewById(R.id.progressBar);

        recyclerAttractions.setLayoutManager(new LinearLayoutManager(this));
        attractionsList = new ArrayList<>();
        attractionsAdapter = new AttractionsAdapter(this, attractionsList, this);
        recyclerAttractions.setAdapter(attractionsAdapter);

        addSampleAttractionsData();

        loadAttractions();
    }

    private void addSampleAttractionsData() {
        // Get current attractions to check if we need to add sample data
        ArrayList<HashMap<String, String>> existingAttractions = database.getAllAttractions();

        if (existingAttractions.isEmpty()) {
            database.addAttraction(
                    "Sunset Beach",
                    "A beautiful beach with golden sands and clear blue water. Perfect for swimming, sunbathing, and watching the stunning sunset views.",
                    1.5,
                    "Nature",
                    "sunset_beach.jpg",
                    "Phone: +1-555-123-4567, Open daily 6 AM - 10 PM"
            );

            database.addAttraction(
                    "Mountain View Trail",
                    "A scenic hiking trail that offers breathtaking views of the surrounding mountains and valleys. The trail is well-maintained and suitable for hikers of all skill levels.",
                    3.2,
                    "Outdoor Activity",
                    "mountain_trail.jpg",
                    "Open 24/7, Guided tours available at Visitor Center"
            );

            database.addAttraction(
                    "City History Museum",
                    "Explore the rich history of the region through interactive exhibits, historical artifacts, and engaging multimedia presentations. The museum features exhibits spanning from ancient times to modern day.",
                    2.0,
                    "Cultural",
                    "history_museum.jpg",
                    "Phone: +1-555-987-6543, Hours: Tue-Sun 9 AM - 5 PM, Closed Mondays"
            );

            database.addAttraction(
                    "Botanical Gardens",
                    "A peaceful oasis featuring thousands of plant species from around the world, arranged in themed gardens. Don't miss the orchid greenhouse and the Japanese meditation garden.",
                    2.8,
                    "Nature",
                    "botanical_garden.jpg",
                    "Phone: +1-555-321-7890, Open daily 8 AM - 7 PM"
            );

            database.addAttraction(
                    "Adventure Park",
                    "An exciting theme park with thrilling rides, water slides, and entertainment for all ages. Features include a 50-meter drop tower, multiple roller coasters, and a water play area.",
                    4.5,
                    "Entertainment",
                    "adventure_park.jpg",
                    "Phone: +1-555-789-0123, Hours: 10 AM - 8 PM weekdays, 9 AM - 10 PM weekends"
            );

            Toast.makeText(this, "Sample attractions added", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAttractions() {
        showLoading(true);

        try {
            ArrayList<HashMap<String, String>> attractions = database.getAllAttractions();
            
            attractionsList.clear();
            attractionsList.addAll(attractions);
            attractionsAdapter.notifyDataSetChanged();

            if (attractionsList.isEmpty()) {
                tvNoAttractions.setVisibility(View.VISIBLE);
            } else {
                tvNoAttractions.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading attractions: " + e.getMessage());
            Toast.makeText(this, "Error loading attractions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            tvNoAttractions.setVisibility(View.VISIBLE);
        } finally {
            showLoading(false);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerAttractions.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (isLoading) {
            tvNoAttractions.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapButtonClick(HashMap<String, String> attraction) {
        try {
            String attractionName = attraction.get("name");
            if (attractionName != null && !attractionName.isEmpty()) {
                String query = Uri.encode(attractionName);
                Uri uri = Uri.parse("https://www.google.com/search?q=" + query);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Attraction information not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open map: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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