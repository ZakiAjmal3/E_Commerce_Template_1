package com.examatlas.crownpublication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity {
    ImageView backImgBtn;
    ImageView imgEdit;
    TextView nameTxt,emailTxt,phoneTxt;
    BottomNavigationView bottom_navigation;
    ProgressBar profileProgressBar;
    LinearLayout profileLinearLayout;
    SessionManager sessionManager;
    public String currentFrag = "PROFILE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backImgBtn = findViewById(R.id.backImgBtn);
        imgEdit = findViewById(R.id.imgEdit);
        nameTxt = findViewById(R.id.nameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        profileProgressBar = findViewById(R.id.profileProgress);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);

        sessionManager = new SessionManager(this);

        // Retrieve user data
        String userName = sessionManager.getUserData().get("name");
        String userEmail = sessionManager.getUserData().get("email");
        String userPhone = sessionManager.getUserData().get("mobile");

        // Set the text in TextViews
        nameTxt.setText(userName != null ? userName : "N/A");
        emailTxt.setText(userEmail != null ? userEmail : "N/A");
        phoneTxt.setText(userPhone != null ? userPhone : "N/A");

        bottom_navigation.setSelectedItemId(R.id.profile);
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    currentFrag = "HOME";
                    Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.cart) {
                    bottom_navigation.setLabelFor(R.id.cart);
                    currentFrag = "CART";
                    Intent intent = new Intent(ProfileActivity.this, CartViewActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.orderHistory) {
                    currentFrag = "ORDER";
                    Intent intent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profileProgressBar.setVisibility(View.GONE);
        profileLinearLayout.setVisibility(View.VISIBLE);
    }
    protected void onResume() {
        super.onResume();
        bottom_navigation.setSelectedItemId(R.id.profile);
    }
}