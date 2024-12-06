package com.examatlas.crownpublication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView backImgBtn;
    ImageView imgEdit;
    TextView nameTxt,emailTxt,phoneTxt;
    ProgressBar profileProgressBar;
    LinearLayout profileLinearLayout;
    SessionManager sessionManager;
    public String currentFrag = "PROFILE";
    String userName,userEmail,userPhone;
    String authToken,userId;
    String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backImgBtn = findViewById(R.id.backImgBtn);
        imgEdit = findViewById(R.id.imgEdit);
        nameTxt = findViewById(R.id.nameTxt);
        emailTxt = findViewById(R.id.emailTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        profileProgressBar = findViewById(R.id.profileProgress);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);

        sessionManager = new SessionManager(this);

        authToken = sessionManager.getUserData().get("authToken");
        userId = sessionManager.getUserData().get("user_id");

        // Retrieve user data
        userName = sessionManager.getUserData().get("name");
        userEmail = sessionManager.getUserData().get("email");
        userPhone = sessionManager.getUserData().get("mobile");

        // Set the text in TextViews
        nameTxt.setText(userName != null ? userName : "N/A");
        emailTxt.setText(userEmail != null ? userEmail : "N/A");
        phoneTxt.setText(userPhone != null ? userPhone : "N/A");

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        profileProgressBar.setVisibility(View.GONE);
        profileLinearLayout.setVisibility(View.VISIBLE);

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialogBox();
            }
        });

    }
    Dialog dialog;
    EditText nameEditText,emailEditText,numberEditText;
    Button submitBtn;
    ImageView crossBtn;
    private void openEditDialogBox() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.profile_edit_dialog_box);

        nameEditText = dialog.findViewById(R.id.nameEditText);
        emailEditText = dialog.findViewById(R.id.emailEditText);
        numberEditText = dialog.findViewById(R.id.numberEditText);
        submitBtn = dialog.findViewById(R.id.submitBtn);
        crossBtn = dialog.findViewById(R.id.crossBtn);

        nameEditText.setText(userName);
        emailEditText.setText(userEmail);
        numberEditText.setText(userPhone);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendProfileDetails(dialog,nameEditText.getText().toString().trim(),emailEditText.getText().toString().trim(),numberEditText.getText().toString().trim());
            }
        });

        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        dialog.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) dialog.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void sendProfileDetails(Dialog dialog, String name, String email, String phone) {
        serverUrl = Constant.BASE_URL + "user/updateUser/" + userId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("mobile", phone);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("LoginPayload", jsonObject.toString());
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Submitting details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, serverUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            if (status.equals("true")) {
                                String message = response.getString("message");
                                JSONObject jsonObject1 = response.getJSONObject("data");

                                String userId = jsonObject1.getString("_id");
                                userName = jsonObject1.getString("name");
                                userEmail = jsonObject1.getString("email");
                                userPhone = jsonObject1.getString("mobile");
                                String role = jsonObject1.getString("role");
                                String authToken = jsonObject1.getString("token");
                                String createdAt = jsonObject1.getString("createdAt");
                                String updatedAt = jsonObject1.getString("updatedAt");

                                // Set the text in TextViews
                                nameTxt.setText(userName != null ? userName : "N/A");
                                emailTxt.setText(userEmail != null ? userEmail : "N/A");
                                phoneTxt.setText(userPhone != null ? userPhone : "N/A");

                                sessionManager.saveLoginDetails(userId, userName, userEmail, userPhone, role, authToken, createdAt, updatedAt);
                                dialog.dismiss();
                                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse",error.toString());
                progressDialog.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        Log.e("JSON error",jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("LoginActivity", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingleton.getInstance(ProfileActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}