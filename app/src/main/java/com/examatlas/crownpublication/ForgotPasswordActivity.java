package com.examatlas.crownpublication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button submitBtn;
    EditText emailEditTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        submitBtn = findViewById(R.id.btnSubmit);
        emailEditTxt = findViewById(R.id.edtEmail);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!emailEditTxt.getText().toString().isEmpty()) {
                    sendPasswordResetEmail(emailEditTxt.getText().toString());
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        String resetPasswordURL = Constant.BASE_URL + "user/forgotpassword";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, resetPasswordURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        try {
                            // Parse the response (if it's a JSON object or just a simple string)
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean responseStatus = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");

                            if (responseStatus) {
                                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle failure or error response
                                Toast.makeText(ForgotPasswordActivity.this, "Failed to create bulk order: " + responseStatus, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException", e.toString());
                            Toast.makeText(ForgotPasswordActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e("OnErrorResponse", error.toString());
                        Toast.makeText(ForgotPasswordActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Set the parameters for the POST request
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }

            @Override
            public String getBodyContentType() {
                // Set content type to application/x-www-form-urlencoded
                return "application/x-www-form-urlencoded";
            }
        };
        // Add the request to the Volley request queue
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}