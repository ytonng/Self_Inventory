package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class UpFridgeDetail extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_up_fridge_detail, container, false);

        // Get the item name from the arguments
        if (getArguments() != null) {
            String itemName = getArguments().getString("item_name");


            // Display the item name
            TextView textView = view.findViewById(R.id.item_name_text);
            textView.setText("Selected Item: " + itemName);
        }

        return view;
    }
}