package com.example.imagerec;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.content.SharedPreferences;
import android.text.InputType;

public class ProfileFragment extends Fragment {

    private EditText emailEditText, passwordEditText;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        saveButton = view.findViewById(R.id.save_button);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(getContext());

        // SharedPreferences to retrieve the logged-in user's email and password
        sharedPreferences = getActivity().getSharedPreferences("userSession", getContext().MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");  // Retrieve email
        String userPassword = sharedPreferences.getString("password", "");  // Retrieve password

        Log.d("ProfileFragment", "User email retrieved: " + userEmail); // Log the email

        // Pre-fill the EditTexts with the user's current data
        if (!TextUtils.isEmpty(userEmail)) {
            emailEditText.setText(userEmail);  // Pre-fill email
        }
        if (!TextUtils.isEmpty(userPassword)) {
            passwordEditText.setText(userPassword);  // Pre-fill password
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  // Make password visible
        }

        // Set up save button click listener to update user data
        saveButton.setOnClickListener(v -> {
            String newEmail = emailEditText.getText().toString();
            String newPassword = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newPassword)) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update user data in the database
                updateUserData(newEmail, newPassword);
            }
        });

        return view;
    }

    private void loadUserData(String email) {
        // Retrieve user data from the database using the email
        Cursor cursor = databaseHelper.getUserByEmail(email);

        if (cursor != null) {
            Log.d("ProfileFragment", "Cursor returned: " + cursor.getCount() + " rows");

            if (cursor.moveToFirst()) {
                // Get column indices for email and password
                int emailIndex = cursor.getColumnIndex("email");
                int passwordIndex = cursor.getColumnIndex("password");

                // Check if the indices are valid (non-negative)
                if (emailIndex != -1 && passwordIndex != -1) {
                    String currentEmail = cursor.getString(emailIndex);
                    String currentPassword = cursor.getString(passwordIndex);

                    // Pre-fill the EditTexts with the current data
                    emailEditText.setText(currentEmail);
                    passwordEditText.setText(currentPassword);
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  // Make password visible
                } else {
                    // Handle case where columns don't exist
                    Log.d("ProfileFragment", "Invalid column index for email or password");
                    Toast.makeText(getContext(), "Database columns are missing or incorrect", Toast.LENGTH_SHORT).show();
                }

            } else {
                // If no data is found, show an error message
                Log.d("ProfileFragment", "No user data found for email: " + email);
                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
            }

            // Close the cursor when done
            cursor.close();
        } else {
            // If cursor is null, show an error message
            Log.d("ProfileFragment", "Error retrieving data from database");
            Toast.makeText(getContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(String email, String password) {
        // Update the user data in the database
        boolean isUpdated = databaseHelper.updateUserData(email, password);

        if (isUpdated) {
            // Update SharedPreferences as well
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);  // Update email in SharedPreferences
            editor.putString("password", password);  // Update password in SharedPreferences
            editor.apply();  // Save changes

            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }
}
