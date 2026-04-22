package com.example.luxevista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private ArrayList<HashMap<String, String>> roomsList;
    private RoomBookListener listener;

    public RoomAdapter(ArrayList<HashMap<String, String>> roomsList, RoomBookListener listener) {
        this.roomsList = roomsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        HashMap<String, String> room = roomsList.get(position);
        
        holder.tvRoomType.setText(room.get("room_type"));
        holder.tvRoomNumber.setText("Room " + room.get("room_number"));
        
        double price = Double.parseDouble(room.get("price"));
        holder.tvRoomPrice.setText(String.format("$%.2f per night", price));
        
        String features = room.get("features");
        if (features != null && !features.isEmpty()) {
            holder.tvRoomFeatures.setText(features);
            holder.tvRoomFeatures.setVisibility(View.VISIBLE);
        } else {
            holder.tvRoomFeatures.setVisibility(View.GONE);
        }
        
        holder.btnBookRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRoomBooked(room);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView tvRoomType, tvRoomNumber, tvRoomPrice, tvRoomFeatures;
        Button btnBookRoom;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            tvRoomFeatures = itemView.findViewById(R.id.tvRoomFeatures);
            btnBookRoom = itemView.findViewById(R.id.btnBookRoom);
        }
    }

    public interface RoomBookListener {
        void onRoomBooked(HashMap<String, String> room);
    }
} 