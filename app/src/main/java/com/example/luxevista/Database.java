package com.example.luxevista;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "luxevista.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_ROOMS = "rooms";
    private static final String TABLE_SERVICES = "services";
    private static final String TABLE_BOOKINGS = "bookings";
    private static final String TABLE_SERVICE_RESERVATIONS = "service_reservations";
    private static final String TABLE_ATTRACTIONS = "attractions";
    private static final String TABLE_PROMOTIONS = "promotions";
    private static final String TABLE_USER_PREFERENCES = "user_preferences";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT, " +
                "phone TEXT, " +
                "profile_image TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        String createRoomsTable = "CREATE TABLE " + TABLE_ROOMS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_number TEXT NOT NULL UNIQUE, " +
                "room_type TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "capacity INTEGER NOT NULL, " +
                "description TEXT, " +
                "features TEXT, " +
                "image_urls TEXT, " +
                "is_available INTEGER DEFAULT 1)";

        String createServicesTable = "CREATE TABLE " + TABLE_SERVICES +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "service_name TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "description TEXT, " +
                "duration INTEGER, " +
                "image_url TEXT, " +
                "is_available INTEGER DEFAULT 1)";

        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "room_id INTEGER NOT NULL, " +
                "check_in_date TEXT NOT NULL, " +
                "check_out_date TEXT NOT NULL, " +
                "total_price REAL NOT NULL, " +
                "status TEXT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(id), " +
                "FOREIGN KEY (room_id) REFERENCES " + TABLE_ROOMS + "(id))";

        String createServiceReservationsTable = "CREATE TABLE " + TABLE_SERVICE_RESERVATIONS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "service_id INTEGER NOT NULL, " +
                "reservation_date TEXT NOT NULL, " +
                "reservation_time TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(id), " +
                "FOREIGN KEY (service_id) REFERENCES " + TABLE_SERVICES + "(id))";

        String createAttractionsTable = "CREATE TABLE " + TABLE_ATTRACTIONS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "distance REAL, " +
                "category TEXT, " +
                "image_url TEXT, " +
                "contact_info TEXT)";

        String createPromotionsTable = "CREATE TABLE " + TABLE_PROMOTIONS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "discount_amount REAL, " +
                "discount_percentage REAL, " +
                "valid_from TEXT, " +
                "valid_until TEXT, " +
                "promo_code TEXT, " +
                "is_active INTEGER DEFAULT 1)";

        String createUserPreferencesTable = "CREATE TABLE " + TABLE_USER_PREFERENCES +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "preference_key TEXT NOT NULL, " +
                "preference_value TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(id))";

        db.execSQL(createUsersTable);
        db.execSQL(createRoomsTable);
        db.execSQL(createServicesTable);
        db.execSQL(createBookingsTable);
        db.execSQL(createServiceReservationsTable);
        db.execSQL(createAttractionsTable);
        db.execSQL(createPromotionsTable);
        db.execSQL(createUserPreferencesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PREFERENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROMOTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }

    public long registerUser(String username, String email, String password, String fullName, String phone) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        values.put("full_name", fullName);
        values.put("phone", phone);

        SQLiteDatabase db = getWritableDatabase();
        long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        return userId;
    }

    public boolean loginUser(String email, String password) {
        boolean success = false;
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"id"};
        String selection = "email=? AND password=?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        success = cursor.moveToFirst();

        cursor.close();
        db.close();
        return success;
    }

    public int login(String username, String password) {
        return loginUser(username, password) ? 1 : 0;
    }

    public HashMap<String, String> getUserDetails(int userId) {
        HashMap<String, String> user = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"id", "username", "email", "full_name", "phone", "profile_image"};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            user.put("id", cursor.getString(cursor.getColumnIndex("id")));
            user.put("username", cursor.getString(cursor.getColumnIndex("username")));
            user.put("email", cursor.getString(cursor.getColumnIndex("email")));
            user.put("full_name", cursor.getString(cursor.getColumnIndex("full_name")));
            user.put("phone", cursor.getString(cursor.getColumnIndex("phone")));
            user.put("profile_image", cursor.getString(cursor.getColumnIndex("profile_image")));
        }

        cursor.close();
        db.close();
        return user;
    }

    /**
     * Get user ID by email and password
     * @return User ID if found, -1 otherwise
     */
    public int getUserIdByCredentials(String email, String password) {
        int userId = -1;
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"id"};
        String selection = "email=? AND password=?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("id"));
        }

        cursor.close();
        db.close();
        return userId;
    }

    /**
     * Get username by user ID
     */
    public String getUsernameById(int userId) {
        String username = "";
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"username"};
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex("username"));
        }

        cursor.close();
        db.close();
        return username;
    }

    public long addRoom(String roomNumber, String roomType, double price, int capacity, String description, String features, String imageUrls) {
        ContentValues values = new ContentValues();
        values.put("room_number", roomNumber);
        values.put("room_type", roomType);
        values.put("price", price);
        values.put("capacity", capacity);
        values.put("description", description);
        values.put("features", features);
        values.put("image_urls", imageUrls);

        SQLiteDatabase db = getWritableDatabase();
        long roomId = db.insert(TABLE_ROOMS, null, values);
        db.close();
        return roomId;
    }

    public ArrayList<HashMap<String, String>> getAllRooms() {
        ArrayList<HashMap<String, String>> roomsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROOMS, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> room = new HashMap<>();
                room.put("id", cursor.getString(cursor.getColumnIndex("id")));
                room.put("room_number", cursor.getString(cursor.getColumnIndex("room_number")));
                room.put("room_type", cursor.getString(cursor.getColumnIndex("room_type")));
                room.put("price", cursor.getString(cursor.getColumnIndex("price")));
                room.put("capacity", cursor.getString(cursor.getColumnIndex("capacity")));
                room.put("description", cursor.getString(cursor.getColumnIndex("description")));
                room.put("features", cursor.getString(cursor.getColumnIndex("features")));
                room.put("image_urls", cursor.getString(cursor.getColumnIndex("image_urls")));
                room.put("is_available", cursor.getString(cursor.getColumnIndex("is_available")));

                roomsList.add(room);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return roomsList;
    }

    public ArrayList<HashMap<String, String>> getAvailableRooms(String checkInDate, String checkOutDate) {
        ArrayList<HashMap<String, String>> availableRooms = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ROOMS + " WHERE id NOT IN " +
                "(SELECT room_id FROM " + TABLE_BOOKINGS +
                " WHERE (check_in_date <= ? AND check_out_date >= ?) OR " +
                "(check_in_date <= ? AND check_out_date >= ?) OR " +
                "(check_in_date >= ? AND check_out_date <= ?))";

        String[] selectionArgs = {checkOutDate, checkInDate, checkInDate, checkOutDate, checkInDate, checkOutDate};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> room = new HashMap<>();
                room.put("id", cursor.getString(cursor.getColumnIndex("id")));
                room.put("room_number", cursor.getString(cursor.getColumnIndex("room_number")));
                room.put("room_type", cursor.getString(cursor.getColumnIndex("room_type")));
                room.put("price", cursor.getString(cursor.getColumnIndex("price")));
                room.put("capacity", cursor.getString(cursor.getColumnIndex("capacity")));
                room.put("description", cursor.getString(cursor.getColumnIndex("description")));
                room.put("features", cursor.getString(cursor.getColumnIndex("features")));
                room.put("image_urls", cursor.getString(cursor.getColumnIndex("image_urls")));

                availableRooms.add(room);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return availableRooms;
    }

    public long addService(String serviceName, String category, double price, String description, int duration, String imageUrl) {
        ContentValues values = new ContentValues();
        values.put("service_name", serviceName);
        values.put("category", category);
        values.put("price", price);
        values.put("description", description);
        values.put("duration", duration);
        values.put("image_url", imageUrl);

        SQLiteDatabase db = getWritableDatabase();
        long serviceId = db.insert(TABLE_SERVICES, null, values);
        db.close();
        return serviceId;
    }

    public ArrayList<HashMap<String, String>> getServicesByCategory(String category) {
        ArrayList<HashMap<String, String>> servicesList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {"id", "service_name", "price", "description", "duration", "image_url", "is_available"};
        String selection = "category=?";
        String[] selectionArgs = {category};

        Cursor cursor = db.query(TABLE_SERVICES, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> service = new HashMap<>();
                service.put("id", cursor.getString(cursor.getColumnIndex("id")));
                service.put("service_name", cursor.getString(cursor.getColumnIndex("service_name")));
                service.put("price", cursor.getString(cursor.getColumnIndex("price")));
                service.put("description", cursor.getString(cursor.getColumnIndex("description")));
                service.put("duration", cursor.getString(cursor.getColumnIndex("duration")));
                service.put("image_url", cursor.getString(cursor.getColumnIndex("image_url")));
                service.put("is_available", cursor.getString(cursor.getColumnIndex("is_available")));

                servicesList.add(service);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return servicesList;
    }

    public long bookRoom(int userId, int roomId, String checkInDate, String checkOutDate, double totalPrice) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("room_id", roomId);
        values.put("check_in_date", checkInDate);
        values.put("check_out_date", checkOutDate);
        values.put("total_price", totalPrice);
        values.put("status", "confirmed");

        SQLiteDatabase db = getWritableDatabase();
        long bookingId = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return bookingId;
    }

    public long reserveService(int userId, int serviceId, String reservationDate, String reservationTime) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("service_id", serviceId);
        values.put("reservation_date", reservationDate);
        values.put("reservation_time", reservationTime);
        values.put("status", "confirmed");

        SQLiteDatabase db = getWritableDatabase();
        long reservationId = db.insert(TABLE_SERVICE_RESERVATIONS, null, values);
        db.close();
        return reservationId;
    }

    public ArrayList<HashMap<String, String>> getUserBookings(int userId) {
        ArrayList<HashMap<String, String>> bookingsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT b.*, r.room_number, r.room_type, r.price FROM " + TABLE_BOOKINGS +
                " b JOIN " + TABLE_ROOMS + " r ON b.room_id = r.id WHERE b.user_id = ? ORDER BY b.check_in_date";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> booking = new HashMap<>();
                booking.put("id", cursor.getString(cursor.getColumnIndex("id")));
                booking.put("check_in_date", cursor.getString(cursor.getColumnIndex("check_in_date")));
                booking.put("check_out_date", cursor.getString(cursor.getColumnIndex("check_out_date")));
                booking.put("total_price", cursor.getString(cursor.getColumnIndex("total_price")));
                booking.put("status", cursor.getString(cursor.getColumnIndex("status")));
                booking.put("room_number", cursor.getString(cursor.getColumnIndex("room_number")));
                booking.put("room_type", cursor.getString(cursor.getColumnIndex("room_type")));

                bookingsList.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookingsList;
    }

    public ArrayList<HashMap<String, String>> getUserServiceReservations(int userId) {
        ArrayList<HashMap<String, String>> reservationsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT sr.*, s.service_name, s.price, s.duration FROM " + TABLE_SERVICE_RESERVATIONS +
                " sr JOIN " + TABLE_SERVICES + " s ON sr.service_id = s.id WHERE sr.user_id = ? ORDER BY sr.reservation_date, sr.reservation_time";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> reservation = new HashMap<>();
                reservation.put("id", cursor.getString(cursor.getColumnIndex("id")));
                reservation.put("reservation_date", cursor.getString(cursor.getColumnIndex("reservation_date")));
                reservation.put("reservation_time", cursor.getString(cursor.getColumnIndex("reservation_time")));
                reservation.put("status", cursor.getString(cursor.getColumnIndex("status")));
                reservation.put("service_name", cursor.getString(cursor.getColumnIndex("service_name")));
                reservation.put("price", cursor.getString(cursor.getColumnIndex("price")));
                reservation.put("duration", cursor.getString(cursor.getColumnIndex("duration")));

                reservationsList.add(reservation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return reservationsList;
    }

    public long addAttraction(String name, String description, double distance, String category, String imageUrl, String contactInfo) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("distance", distance);
        values.put("category", category);
        values.put("image_url", imageUrl);
        values.put("contact_info", contactInfo);

        SQLiteDatabase db = getWritableDatabase();
        long attractionId = db.insert(TABLE_ATTRACTIONS, null, values);
        db.close();
        return attractionId;
    }

    public ArrayList<HashMap<String, String>> getAllAttractions() {
        ArrayList<HashMap<String, String>> attractionsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTRACTIONS + " ORDER BY distance", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> attraction = new HashMap<>();
                attraction.put("id", cursor.getString(cursor.getColumnIndex("id")));
                attraction.put("name", cursor.getString(cursor.getColumnIndex("name")));
                attraction.put("description", cursor.getString(cursor.getColumnIndex("description")));
                attraction.put("distance", cursor.getString(cursor.getColumnIndex("distance")));
                attraction.put("category", cursor.getString(cursor.getColumnIndex("category")));
                attraction.put("image_url", cursor.getString(cursor.getColumnIndex("image_url")));
                attraction.put("contact_info", cursor.getString(cursor.getColumnIndex("contact_info")));

                attractionsList.add(attraction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attractionsList;
    }

    public long addPromotion(String title, String description, Double discountAmount, Double discountPercentage,
                           String validFrom, String validUntil, String promoCode) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);

        if (discountAmount != null) {
            values.put("discount_amount", discountAmount);
        } else {
            values.putNull("discount_amount");
        }

        if (discountPercentage != null) {
            values.put("discount_percentage", discountPercentage);
        } else {
            values.putNull("discount_percentage");
        }

        values.put("valid_from", validFrom);
        values.put("valid_until", validUntil);
        values.put("promo_code", promoCode);
        values.put("is_active", 1);

        SQLiteDatabase db = getWritableDatabase();
        long promotionId = db.insert(TABLE_PROMOTIONS, null, values);
        db.close();
        return promotionId;
    }

    public ArrayList<HashMap<String, String>> getActivePromotions() {
        ArrayList<HashMap<String, String>> promotionsList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try {
            String currentDate = java.time.LocalDate.now().toString();

            String query = "SELECT * FROM " + TABLE_PROMOTIONS;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> promotion = new HashMap<>();

                    int idIndex = cursor.getColumnIndex("id");
                    int titleIndex = cursor.getColumnIndex("title");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int discountAmountIndex = cursor.getColumnIndex("discount_amount");
                    int discountPercentageIndex = cursor.getColumnIndex("discount_percentage");
                    int validFromIndex = cursor.getColumnIndex("valid_from");
                    int validUntilIndex = cursor.getColumnIndex("valid_until");
                    int promoCodeIndex = cursor.getColumnIndex("promo_code");

                    if (idIndex != -1) {
                        promotion.put("id", cursor.getString(idIndex));
                    }
                    if (titleIndex != -1) {
                        promotion.put("title", cursor.getString(titleIndex));
                    }
                    if (descriptionIndex != -1) {
                        promotion.put("description", cursor.getString(descriptionIndex));
                    }
                    if (discountAmountIndex != -1) {
                        promotion.put("discount_amount", cursor.isNull(discountAmountIndex) ?
                                null : cursor.getString(discountAmountIndex));
                    }
                    if (discountPercentageIndex != -1) {
                        promotion.put("discount_percentage", cursor.isNull(discountPercentageIndex) ?
                                null : cursor.getString(discountPercentageIndex));
                    }
                    if (validFromIndex != -1) {
                        promotion.put("valid_from", cursor.getString(validFromIndex));
                    }
                    if (validUntilIndex != -1) {
                        promotion.put("valid_until", cursor.getString(validUntilIndex));
                    }
                    if (promoCodeIndex != -1) {
                        promotion.put("promo_code", cursor.getString(promoCodeIndex));
                    }

                    promotionsList.add(promotion);
                } while (cursor.moveToNext());
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return promotionsList;
    }
}
