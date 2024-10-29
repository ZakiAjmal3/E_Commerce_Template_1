package com.examatlas.crownpublication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.examatlas.crownpublication.Utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(MainActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }else {
                    Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        },2000);

    }
}