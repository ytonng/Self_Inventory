package com.example.imagerec;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class RecommendDishes extends Fragment {

    private String dishName;
    private String dishImageUrl;
    private String dishDescription;
    private String dishIngredients;
    private String dishVideoUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_dishes, container, false);

        // Retrieve dish data from arguments
        if (getArguments() != null) {
            dishName = getArguments().getString("dish_name");
            dishImageUrl = getArguments().getString("dish_image_url");
            dishIngredients = getArguments().getString("dish_ingredients");
            dishDescription = getArguments().getString("dish_description");
            dishVideoUrl = getArguments().getString("dish_video_url");
        }

        // Set up ImageButton for Back
        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragment_container, new HomeFragment()) // Replace with HomeFragment
                    .addToBackStack(null) // Add to back stack so we can go back to RecommendDishes if needed
                    .commit();
        });

        // Update UI with the dish data
        TextView textView = view.findViewById(R.id.dish_name);
        textView.setText(dishName);

        TextView descriptionTextView = view.findViewById(R.id.steps);
        descriptionTextView.setText(dishDescription);

        // Set the ingredients data
        TextView ingredientsTextView = view.findViewById(R.id.ingredients);
        ingredientsTextView.setText(dishIngredients);

        // Set the dish image using Glide
        ImageView dishImage = view.findViewById(R.id.dish_image);
        Glide.with(getContext())
                .load(dishImageUrl)
                .into(dishImage);

        // Load the YouTube video into the WebView
        WebView youtubeWebView = view.findViewById(R.id.youtube_video);

        // You should pass the embed URL for the video
        String embedUrl = "https://www.youtube.com/embed/" + extractVideoId(dishVideoUrl);

        youtubeWebView.getSettings().setJavaScriptEnabled(true);
        youtubeWebView.loadUrl(embedUrl);

        return view;
    }

    // Helper method to extract video ID from YouTube URL
    private String extractVideoId(String youtubeUrl) {
        String videoId = null;
        if (youtubeUrl != null && youtubeUrl.contains("v=")) {
            int startIndex = youtubeUrl.indexOf("v=") + 2;
            int endIndex = youtubeUrl.indexOf("&", startIndex);
            if (endIndex == -1) {
                endIndex = youtubeUrl.length();
            }
            videoId = youtubeUrl.substring(startIndex, endIndex);
        }
        return videoId;
    }
}
