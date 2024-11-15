package com.example.imagerec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name
    public static final String databaseName = "imagerec.db";

    // Dishes table and its columns
    public static final String DISHES_TABLE = "dishes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INGREDIENTS = "ingredients";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_VIDEO_URL = "video_url";
    public static final String COLUMN_IMAGE_URL ="image_url" ;

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users (email TEXT PRIMARY KEY, password TEXT)");

        // Create dishes table with an image URL column
        db.execSQL("CREATE TABLE dishes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "ingredients TEXT, " +
                "description TEXT, " +
                "video_url TEXT, " +
                "image_url TEXT" +  // Add column for image URL
                ")");
        // Create items table
        db.execSQL("CREATE TABLE items (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +  // Foreign key to the user
                "item_name TEXT, " +
                "storage_location TEXT, " +
                "quantity INTEGER, " +
                "expiry_date TEXT, " + // Expiry date for the item
                "item_image BLOB, " +  // Image uploaded by the user
                "icon_image BLOB, " +  // Icon image for the item
                "shelf_life TEXT, " +  // Shelf life (days or date)
                "nutritionInfo TEXT, " +  // Nutritional information
                "storage_date TEXT, " +  // Date the item was stored
                "FOREIGN KEY(user_email) REFERENCES users(email)" +  // Ensure referential integrity
                ")");

        // Check if dishes table is empty, and insert sample dishes if it is
        if (isDishesTableEmpty(db)) {
            insertSampleDishes(db);
        }
    }

    // Method to check if the dishes table is empty
    private boolean isDishesTableEmpty(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + DISHES_TABLE, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            return count == 0; // Return true if table is empty
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS " + DISHES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS items"); // Drop items table as well
        onCreate(db);
    }

    public long insertItem(String userEmail, String itemName, String storageLocation, int quantity,
                           String expiryDate, byte[] itemImage, byte[] iconImage, String shelfLife,
                           String nutritionInfo, String storageDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("user_email", userEmail);
        contentValues.put("item_name", itemName);
        contentValues.put("storage_location", storageLocation);
        contentValues.put("quantity", quantity);
        contentValues.put("expiry_date", expiryDate);
        contentValues.put("item_image", itemImage);
        contentValues.put("icon_image", iconImage);
        contentValues.put("shelf_life", shelfLife);
        contentValues.put("nutritionInfo", nutritionInfo); // This should be correct as per the table definition
        contentValues.put("storage_date", storageDate);

        return db.insert("items", null, contentValues);
    }

    public Cursor getItemsForLowFridge() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Fetch items where storage_location is 'lowfridge'
        return db.rawQuery("SELECT * FROM items WHERE storage_location = ?", new String[]{"Low Fridge"});
    }
    public Cursor getItemsForUpFridge() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Fetch items where storage_location is 'upfridge'
        return db.rawQuery("SELECT * FROM items WHERE storage_location = ?", new String[]{"Up Fridge"});
    }

    public Cursor getItemsForUserLowFridge(String userEmail, int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM items WHERE user_email = ? AND item_id = ?";
        return db.rawQuery(query, new String[]{userEmail, String.valueOf(itemId)});
    }
    public boolean updateFridgeItem(int itemId, String itemName, String storageLocation, int quantity,
                                    String expiryDate, byte[] itemImage, byte[] iconImage, String shelfLife,
                                    String nutritionInfo, String storageDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add the new values for the columns
        contentValues.put("item_name", itemName);
        contentValues.put("storage_location", storageLocation);
        contentValues.put("quantity", quantity);
        contentValues.put("expiry_date", expiryDate);
        contentValues.put("item_image", itemImage);
        contentValues.put("icon_image", iconImage);
        contentValues.put("shelf_life", shelfLife);
        contentValues.put("nutritionInfo", nutritionInfo);
        contentValues.put("storage_date", storageDate);

        // Update the record with the specific item_id
        int rowsAffected = db.update("items", contentValues, "item_id = ?", new String[]{String.valueOf(itemId)});

        return rowsAffected > 0;  // Returns true if at least one row was updated
    }


    // Insert sample dishes data into the database
    private void insertSampleDishes(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();

        // Dish 1: Spaghetti Carbonara
        contentValues.put(COLUMN_NAME, "Spaghetti Carbonara");
        contentValues.put(COLUMN_INGREDIENTS, "Spaghetti, Eggs, Bacon, Parmesan cheese");
        contentValues.put(COLUMN_DESCRIPTION, "Boil spaghetti, cook bacon, mix with eggs and cheese.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/3AAdKl1UYZs");
        contentValues.put(COLUMN_IMAGE_URL, "https://images.services.kitchenstories.io/0jDEDt2yRmfbZPbpRXG3a2ATGpk=/1080x0/filters:quality(85)/images.kitchenstories.io/wagtailOriginalImages/R2568-photo-final-_0.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        // Dish 2: Chicken Alfredo
        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Chicken Alfredo");
        contentValues.put(COLUMN_INGREDIENTS, "Chicken, Alfredo sauce, Fettuccine");
        contentValues.put(COLUMN_DESCRIPTION, "Cook chicken, mix with Alfredo sauce and pasta.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/F7CU0qBdj04");
        contentValues.put(COLUMN_IMAGE_URL, "https://allrecipes.com/thmb/d9KfT0VVhehtF8GMl3t9Em9iuBs=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/6627608-c37d68c85eef4b07b55db54339921b00.jpg");  // Corrected image URL
        db.insert(DISHES_TABLE, null, contentValues);

        // Dish 3: Caesar Salad
        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Caesar Salad");
        contentValues.put(COLUMN_INGREDIENTS, "Lettuce, Croutons, Caesar dressing, Parmesan cheese");
        contentValues.put(COLUMN_DESCRIPTION, "Toss lettuce with croutons, dressing, and cheese.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/7Mi8DmbAE74");
        contentValues.put(COLUMN_IMAGE_URL, "https://www.allrecipes.com/thmb/GKJL13Wb8TZ9hpJ9c70v0aNXsyQ=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/229063-Classic-Restaurant-Caesar-Salad-ddmfs-4x3-231-89bafa5e54dd4a8c933cf2a5f9f12a6f.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

        // Continue with the rest of your dishes...
        // Dish 9: Mushroom Risotto
        contentValues.clear();
        contentValues.put(COLUMN_NAME, "Mushroom Risotto");
        contentValues.put(COLUMN_INGREDIENTS, "Rice, Mushrooms, Chicken broth, Parmesan cheese");
        contentValues.put(COLUMN_DESCRIPTION, "Cook rice with mushrooms and chicken broth.");
        contentValues.put(COLUMN_VIDEO_URL, "https://youtu.be/ju9H1RlYNxk");
        contentValues.put(COLUMN_IMAGE_URL, "https://www.eatingwell.com/thmb/Mc1Yo_NWBctQAoYv72NEd2KijRs=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/mushroom-risotto-beauty-8025316-4000x4000-203a642728ca49c895b487d6df0dc6e3.jpg");  // Add image URL
        db.insert(DISHES_TABLE, null, contentValues);

    }
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        // Log or check if cursor is null
        if (cursor != null) {
            Log.d("DatabaseHelper", "Cursor size: " + cursor.getCount());
        }
        return cursor;
    }



    // Method to update user data (email and password)
    public boolean updateUserData(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);

        // Update the user record where the email matches
        int result = db.update("users", contentValues, "email = ?", new String[]{email});
        return result > 0;  // Return true if at least one row was updated
    }


    // Method to get all dishes from the database
    public Cursor getAllDishes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DISHES_TABLE, null);
    }
    public Cursor getRandomDishes(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DISHES_TABLE + " ORDER BY RANDOM() LIMIT ?", new String[]{String.valueOf(limit)});
    }

    // Method to get a specific dish by its name
    public Cursor getDishByName(String dishName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + DISHES_TABLE + " WHERE " + COLUMN_NAME + " = ?", new String[]{dishName});
    }

    // Method to insert a user
    public Boolean insertData(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = MyDatabase.insert("users", null, contentValues);
        return result != -1;
    }

    // Method to check if email exists
    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    // Method to check if email and password match
    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }
}
