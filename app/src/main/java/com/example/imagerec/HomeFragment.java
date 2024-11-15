package com.example.imagerec;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import java.util.ArrayList;
import java.util.Random;
import com.bumptech.glide.Glide;
import android.util.Log;


public class HomeFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Set up the image slider
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slideimg1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slideimg2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slideimg3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slideimg4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slideimg5, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        // Set up ImageButtons with click listeners to open RecommendDishes fragment with random dish data



        // Load random dishes into the ImageButtons
        loadRandomDishes(view);

        return view;
    }

    private void loadRandomDishes(View view) {
        // Get the list of all dishes from the database
        Cursor cursor = databaseHelper.getAllDishes();
        if (cursor != null && cursor.getCount() > 0) {
            // Create a list to keep track of selected positions to avoid duplicates
            ArrayList<Integer> selectedPositions = new ArrayList<>();

            // Randomly select 4 unique dishes
            loadRandomDishForButton(view, R.id.btn1, cursor, selectedPositions);
            loadRandomDishForButton(view, R.id.btn2, cursor, selectedPositions);
            loadRandomDishForButton(view, R.id.btn3, cursor, selectedPositions);
            loadRandomDishForButton(view, R.id.btn4, cursor, selectedPositions);
        }
    }

    private void loadRandomDishForButton(View view, int buttonId, Cursor cursor, ArrayList<Integer> selectedPositions) {
        Random random = new Random();
        int randomPosition;

        // Keep picking a random position until it's unique (not in the selectedPositions list)
        do {
            randomPosition = random.nextInt(cursor.getCount());
        } while (selectedPositions.contains(randomPosition));

        selectedPositions.add(randomPosition);
        cursor.moveToPosition(randomPosition);

        // Get dish data from cursor
        int imageColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL);
        int nameColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
        int descriptionColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);
        int ingredientsColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_INGREDIENTS);
        int videoColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_VIDEO_URL); // Assuming column exists

        if (imageColumnIndex != -1 && nameColumnIndex != -1 && descriptionColumnIndex != -1 && ingredientsColumnIndex != -1 && videoColumnIndex != -1) {
            String dishName = cursor.getString(nameColumnIndex);
            String dishImageUrl = cursor.getString(imageColumnIndex);
            String dishDescription = cursor.getString(descriptionColumnIndex);
            String dishIngredients = cursor.getString(ingredientsColumnIndex);
            String dishVideoUrl = cursor.getString(videoColumnIndex);

            // Find the ImageButton and set the image dynamically
            ImageButton dishButton = view.findViewById(buttonId);
            Glide.with(getContext())
                    .load(dishImageUrl)
                    .placeholder(R.drawable.cabonara) // Add placeholder image
                    .into(dishButton);

            // Set the OnClickListener for the button to open the RecommendDishes fragment with the correct data
            dishButton.setOnClickListener(v -> openRecommendDishesFragment(view, dishName, dishImageUrl, dishDescription, dishIngredients,dishVideoUrl));
        } else {
            Log.e("HomeFragment", "One or more columns are missing in the cursor");
        }
    }


    private int getButtonIdByIndex(int index) {
        switch (index) {
            case 0:
                return R.id.btn1;
            case 1:
                return R.id.btn2;
            case 2:
                return R.id.btn3;
            case 3:
                return R.id.btn4;
            default:
                return -1;  // Invalid index
        }
    }

    private void openRecommendDishesFragment(View clickedButton, String dishName, String dishImageUrl, String dishDescription, String dishIngredients,String dishVideoUrl) {
        // Create new instance of RecommendDishes fragment
        RecommendDishes recommendDishesFragment = new RecommendDishes();

        // Create a bundle to hold the dish data
        Bundle bundle = new Bundle();
        bundle.putString("dish_name", dishName);
        bundle.putString("dish_image_url", dishImageUrl);
        bundle.putString("dish_description", dishDescription);
        bundle.putString("dish_ingredients", dishIngredients);
        bundle.putString("dish_video_url", dishVideoUrl);// Add ingredients data
        recommendDishesFragment.setArguments(bundle);

        // Replace current fragment with RecommendDishes fragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right); // Optional animation
        transaction.replace(R.id.fragment_container, recommendDishesFragment);
        transaction.addToBackStack(null); // Add to back stack so user can navigate back
        transaction.commit();
    }


}
