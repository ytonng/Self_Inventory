package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class RefrigeratorFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refrigerator, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        fab = view.findViewById(R.id.fab);

        // Set up the BottomNavigationView listener as before
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.btm_nav_up) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.refrigerator_fragment_container, new UpFridge())
                        .commit();
            } else if (item.getItemId() == R.id.btm_nav_down) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.refrigerator_fragment_container, new LowFridge())
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.refrigerator_fragment_container, new UpFridge())
                    .commit();
        }

        // Set up FloatingActionButton click listener to show BottomSheetDialog
        fab.setOnClickListener(v -> showBottomSheetDialog());

        return view;
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());

        // Inflate the bottom sheet layout
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheetlayout, null);

        // Apply transparent background to the bottom sheet content
        bottomSheetView.setBackgroundResource(R.drawable.dialogbg); // Background with rounded corners

        // Set transparent background for the dialog window itself
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        bottomSheetDialog.setContentView(bottomSheetView);

        // Initialize the views within the bottom sheet
        LinearLayout videoLayout = bottomSheetView.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = bottomSheetView.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = bottomSheetView.findViewById(R.id.layoutLive);

        // Set click listeners for each option in the bottom sheet
        videoLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(requireContext(), "Upload a Video is clicked", Toast.LENGTH_SHORT).show();
        });

        shortsLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(requireContext(), "Create a short is clicked", Toast.LENGTH_SHORT).show();
        });

        liveLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(requireContext(), "Go live is clicked", Toast.LENGTH_SHORT).show();
        });


        // Show the BottomSheetDialog
        bottomSheetDialog.show();
    }
}