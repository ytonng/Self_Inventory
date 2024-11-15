package com.example.imagerec;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class UpFridgeDetail extends Fragment {

    private Bitmap itemImage;
    private String itemName;
    private Bitmap itemIcon;
    private String storageLocation;
    private String storageDate;
    private String expirationDate;
    private int shelfLife;
    private int quantity;
    private String nutritionalInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_up_fridge_detail, container, false);

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

        // Initialize UI components
        ImageView itemImageView = view.findViewById(R.id.itemImage1);
        ImageView iconImageView = view.findViewById(R.id.chooseIconImageView1); // Corrected to ImageView
        EditText itemNameField = view.findViewById(R.id.itemnameField1);
        Spinner storageLocationSpinner = view.findViewById(R.id.storageLocationSpinner1);
        EditText storageDateField = view.findViewById(R.id.storageDate1);
        EditText expirationDateField = view.findViewById(R.id.expirationDate1);
        NumberPicker shelfLifePicker = view.findViewById(R.id.shelfLifePicker1);
        NumberPicker quantityPicker = view.findViewById(R.id.quantityPicker1);
        EditText nutritionalInfoField = view.findViewById(R.id.nutritionalInfo1);

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

        return view;
    }
}
