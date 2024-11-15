package com.example.imagerec;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class FridgeDetail extends Fragment {
    private int itemId;
    private Bitmap itemImage;
    private String itemName;
    private Bitmap itemIcon;
    private String storageLocation;
    private String storageDate;
    private String expirationDate;
    private int shelfLife;
    private int quantity;
    private String nutritionalInfo;
    private DatabaseHelper databaseHelper; // Initialize DatabaseHelper

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fridge_detail, container, false);

        // Retrieve item details from the arguments passed
        if (getArguments() != null) {
            itemName = getArguments().getString("item_name", "Default Name");
            itemImage = getArguments().getParcelable("item_image");
            itemIcon = getArguments().getParcelable("icon_image");
            storageLocation = getArguments().getString("storage_location", "Default Location");
            storageDate = getArguments().getString("storage_date", "Unknown Date");
            expirationDate = getArguments().getString("expiration_date", "Unknown Date");
            shelfLife = getArguments().getInt("shelf_life", 7); // Default to 7 days
            quantity = getArguments().getInt("quantity", 1); // Default to 1
            nutritionalInfo = getArguments().getString("nutritional_info", "N/A");
        }

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Initialize UI components
        ImageView itemImageView = view.findViewById(R.id.itemImage);
        ImageView iconImageView = view.findViewById(R.id.chooseIconImageView); // Corrected to ImageView
        EditText itemNameField = view.findViewById(R.id.itemnameField);
        Spinner storageLocationSpinner = view.findViewById(R.id.storageLocationSpinner);
        EditText storageDateField = view.findViewById(R.id.storageDate);
        EditText expirationDateField = view.findViewById(R.id.expirationDate);
        NumberPicker shelfLifePicker = view.findViewById(R.id.shelfLifePicker);
        NumberPicker quantityPicker = view.findViewById(R.id.quantityPicker);
        EditText nutritionalInfoField = view.findViewById(R.id.nutritionalInfo);

        // Set values to UI components
        if (itemIcon != null) {
            iconImageView.setImageBitmap(itemIcon);
        }

        if (itemImage != null) {
            itemImageView.setImageBitmap(itemImage);
        }

        itemNameField.setText(itemName);
        storageDateField.setText(storageDate);
        expirationDateField.setText(expirationDate);
        nutritionalInfoField.setText(nutritionalInfo);

        // Set default values for NumberPickers
        shelfLifePicker.setMinValue(1);
        shelfLifePicker.setMaxValue(365); // Assuming shelf life is in days
        shelfLifePicker.setValue(shelfLife);

        quantityPicker.setMinValue(1);
        quantityPicker.setMaxValue(100); // Or adjust according to your app logic
        quantityPicker.setValue(quantity);

        // Setup spinner for storage location
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.storage_location_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storageLocationSpinner.setAdapter(adapter);

        // Set storage location spinner selection
        if (storageLocation != null) {
            int spinnerPosition = adapter.getPosition(storageLocation);
            storageLocationSpinner.setSelection(spinnerPosition);
        }

        // Set the save button's onClickListener
        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveData(itemNameField, storageLocationSpinner, storageDateField, expirationDateField, shelfLifePicker, quantityPicker, nutritionalInfoField, itemImageView, iconImageView));

        return view;
    }

    // Method to save updated data into the database
    private void saveData(EditText itemNameField, Spinner storageLocationSpinner, EditText storageDateField, EditText expirationDateField,
                          NumberPicker shelfLifePicker, NumberPicker quantityPicker, EditText nutritionalInfoField,
                          ImageView itemImageView, ImageView iconImageView) {
        // Retrieve the updated data from the UI
        String updatedItemName = itemNameField.getText().toString();
        String updatedStorageLocation = storageLocationSpinner.getSelectedItem().toString();
        String updatedStorageDate = storageDateField.getText().toString();
        String updatedExpirationDate = expirationDateField.getText().toString();
        int updatedShelfLife = shelfLifePicker.getValue(); // Keep as int, will convert to String for DB
        int updatedQuantity = quantityPicker.getValue();
        String updatedNutritionalInfo = nutritionalInfoField.getText().toString();

        // Get Bitmap images from ImageViews
        Bitmap updatedItemImage = ((BitmapDrawable) itemImageView.getDrawable()).getBitmap();
        Bitmap updatedItemIcon = ((BitmapDrawable) iconImageView.getDrawable()).getBitmap();

        // Convert shelfLife to String as the method expects it to be a String
        String shelfLifeString = String.valueOf(updatedShelfLife);

        // Convert Bitmap images to byte arrays (for storage in the database)
        ByteArrayOutputStream itemImageStream = new ByteArrayOutputStream();
        updatedItemImage.compress(Bitmap.CompressFormat.PNG, 100, itemImageStream);
        byte[] itemImageBytes = itemImageStream.toByteArray();

        ByteArrayOutputStream iconImageStream = new ByteArrayOutputStream();
        updatedItemIcon.compress(Bitmap.CompressFormat.PNG, 100, iconImageStream);
        byte[] iconImageBytes = iconImageStream.toByteArray();

        // Update the database with the new data
        boolean success = databaseHelper.updateFridgeItem(itemId, updatedItemName, updatedStorageLocation, updatedQuantity,
                updatedExpirationDate, itemImageBytes, iconImageBytes, shelfLifeString, updatedNutritionalInfo, updatedStorageDate);

        if (success) {
            // Show success message
            Toast.makeText(getContext(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Show error message
            Toast.makeText(getContext(), "Failed to update item", Toast.LENGTH_SHORT).show();
        }
    }

}
