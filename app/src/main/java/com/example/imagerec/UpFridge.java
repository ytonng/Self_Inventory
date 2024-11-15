package com.example.imagerec;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class UpFridge extends Fragment {

    private DatabaseHelper databaseHelper;
    private GridLayout gridLayout;
    private SharedPreferences sharedPreferences;  // Declare SharedPreferences

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_low_fridge, container, false);

        // Initialize the GridLayout
        gridLayout = view.findViewById(R.id.gridLayout);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize SharedPreferences for retrieving email
        sharedPreferences = getActivity().getSharedPreferences("userSession", getContext().MODE_PRIVATE);

        // Clear any previous views in the grid
        gridLayout.removeAllViews();

        // Get items for low fridge
        Cursor cursor = databaseHelper.getItemsForUpFridge();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Get the index for the columns
                int itemNameIndex = cursor.getColumnIndex("item_name");
                int iconImageIndex = cursor.getColumnIndex("icon_image");
                int itemIdIndex = cursor.getColumnIndex("item_id"); // Assuming you have item_id column

                if (itemNameIndex != -1 && iconImageIndex != -1 && itemIdIndex != -1) {
                    // Fetch item details
                    String itemName = cursor.getString(itemNameIndex);
                    byte[] iconImage = cursor.getBlob(iconImageIndex);
                    int itemId = cursor.getInt(itemIdIndex);  // Get the itemId from the cursor

                    // Convert icon image (byte array) to Bitmap
                    Bitmap iconBitmap = BitmapFactory.decodeByteArray(iconImage, 0, iconImage.length);

                    // Create and configure the layout for each item
                    LinearLayout itemLayout = new LinearLayout(getContext());
                    itemLayout.setOrientation(LinearLayout.VERTICAL);
                    itemLayout.setGravity(Gravity.CENTER);
                    itemLayout.setLayoutParams(new GridLayout.LayoutParams());

                    // Create the image button for the item
                    ImageButton itemButton = new ImageButton(getContext());
                    itemButton.setImageBitmap(iconBitmap);
                    itemButton.setContentDescription(itemName);
                    itemButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    itemButton.setOnClickListener(v -> openDetailFragment(itemId));  // Pass itemId here

                    // Create a TextView for the item name
                    TextView itemNameText = new TextView(getContext());
                    itemNameText.setText(itemName);
                    itemNameText.setTextSize(14);
                    itemNameText.setGravity(Gravity.CENTER);

                    // Add the button and text to the item layout
                    itemLayout.addView(itemButton);
                    itemLayout.addView(itemNameText);

                    // Add the item layout to the grid
                    gridLayout.addView(itemLayout);
                }
            }
            cursor.close();
        } else {
            // Handle the case where no items are found
            Log.d("LowFridge", "No items found for lowfridge storage.");
        }

        return view;
    }

    public void openDetailFragment(int itemId) {
        // Retrieve the user's email from SharedPreferences
        String userEmail = sharedPreferences.getString("email", "");  // Retrieve email

        if (userEmail.isEmpty()) {
            // Handle case when the email is not available
            Log.d("LowFridge", "User email is not available");
            // Optionally, prompt the user to log in or navigate to login screen
            Toast.makeText(getContext(), "Please log in to view your fridge items", Toast.LENGTH_LONG).show();
            return;
        }

        // Retrieve data for the selected item and user
        Cursor cursor = databaseHelper.getItemsForUserLowFridge(userEmail, itemId);

        if (cursor != null && cursor.moveToFirst()) {  // Ensure cursor has data
            int itemNameIndex = cursor.getColumnIndex("item_name");
            int storageLocationIndex = cursor.getColumnIndex("storage_location");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int expiryDateIndex = cursor.getColumnIndex("expiry_date");
            int itemImageIndex = cursor.getColumnIndex("item_image");
            int iconImageIndex = cursor.getColumnIndex("icon_image");
            int shelfLifeIndex = cursor.getColumnIndex("shelf_life");
            int nutritionInfoIndex = cursor.getColumnIndex("nutritionInfo");
            int storageDateIndex = cursor.getColumnIndex("storage_date");

            if (itemNameIndex != -1 && storageLocationIndex != -1 && quantityIndex != -1 &&
                    expiryDateIndex != -1 && itemImageIndex != -1 && iconImageIndex != -1 &&
                    shelfLifeIndex != -1 && nutritionInfoIndex != -1 && storageDateIndex != -1) {

                String itemName = cursor.getString(itemNameIndex);
                String storageLocation = cursor.getString(storageLocationIndex);
                int quantity = cursor.getInt(quantityIndex);
                String expiryDate = cursor.getString(expiryDateIndex);
                byte[] itemImage = cursor.getBlob(itemImageIndex);
                byte[] iconImage = cursor.getBlob(iconImageIndex);
                String shelfLife = cursor.getString(shelfLifeIndex); // This might contain "4 days"
                String nutritionInfo = cursor.getString(nutritionInfoIndex);
                String storageDate = cursor.getString(storageDateIndex);

                // Remove non-numeric characters (e.g., " days") from the shelfLife string
                String numericShelfLife = shelfLife.replaceAll("[^0-9]", ""); // Removes anything that's not a number
                int shelfLifeValue = 7; // Default value if parsing fails

                try {
                    shelfLifeValue = Integer.parseInt(numericShelfLife);
                } catch (NumberFormatException e) {
                    Log.d("LowFridge", "Invalid shelf life format, using default: " + e.getMessage());
                }

                // Create a new instance of FridgeDetail and pass the item data to it
                FridgeDetail fridgeDetailFragment = new FridgeDetail();
                Bundle args = new Bundle();
                args.putString("item_name", itemName);
                args.putParcelable("item_image", BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length));
                args.putParcelable("icon_image", BitmapFactory.decodeByteArray(iconImage, 0, iconImage.length));
                args.putString("storage_location", storageLocation);
                args.putString("storage_date", storageDate);
                args.putString("expiration_date", expiryDate);
                args.putInt("shelf_life", shelfLifeValue); // Use the parsed shelf life
                args.putInt("quantity", quantity);
                args.putString("nutritional_info", nutritionInfo);
                fridgeDetailFragment.setArguments(args);

                // Begin transaction to display the FridgeDetail fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fridgeDetailFragment);
                transaction.addToBackStack(null);  // Optionally add to back stack for navigation back
                transaction.commit();
            } else {
                Log.d("LowFridge", "One or more columns are missing in the cursor.");
            }

            cursor.close();  // Don't forget to close the cursor
        } else {
            Log.d("LowFridge", "No data found for the selected item.");
        }
    }

}
