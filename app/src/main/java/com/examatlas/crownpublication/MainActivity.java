package com.examatlas.crownpublication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.examatlas.crownpublication.Utils.SessionManager;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    TextView crownTxt;
    TextView publicationTxt;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crownTxt = findViewById(R.id.crownTxt);
        publicationTxt = findViewById(R.id.publicationTxt);
        logo = findViewById(R.id.logo);

        sessionManager = new SessionManager(MainActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_activity_logo_rotation);
                Animation logo_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.toptobottom);
                Animation logo_text_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottomtotop);
                crownTxt.startAnimation(logo_object);
                publicationTxt.startAnimation(logo_text_object);
                logo.startAnimation(loadAnimation);
                // Delay the startActivity to match the animation duration
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }, 2000);// Use the same duration as the animation
            }
        }, 0);
    }
}