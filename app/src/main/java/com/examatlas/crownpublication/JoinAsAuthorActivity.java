package com.examatlas.crownpublication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class JoinAsAuthorActivity extends AppCompatActivity {
    ImageView backBtn;
    EditText authorNameEditText, positionEditText, emailEditText,contactNumberEditText, titleOfBookEditText, topicEditText, descriptionEditText, previousWorkEditText;
    Button submitBtn;
    SessionManager sessionManager;
    String authToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_as_author);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        authorNameEditText = findViewById(R.id.authorNameEditText);
        positionEditText = findViewById(R.id.positionEditText);
        emailEditText = findViewById(R.id.emailEditText);
        contactNumberEditText = findViewById(R.id.contactNumberEditText);
        titleOfBookEditText = findViewById(R.id.titleOfBookEditText);
        topicEditText = findViewById(R.id.topicEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        previousWorkEditText = findViewById(R.id.previousWorkEditText);

        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authorName = authorNameEditText.getText().toString();
                String position = positionEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String contactNumber = contactNumberEditText.getText().toString();
                String titleOfBook = titleOfBookEditText.getText().toString();
                String topic = topicEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String previousWork = previousWorkEditText.getText().toString();

                if (authorName.isEmpty() || position.isEmpty() || email.isEmpty() || contactNumber.isEmpty() || titleOfBook.isEmpty() || topic.isEmpty() || description.isEmpty() || previousWork.isEmpty()) {
                    Toast.makeText(JoinAsAuthorActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    registerAsAuthor(authorName, position, email, contactNumber, titleOfBook, topic, description, previousWork);
                }
            }
        });

    }

    private void registerAsAuthor(String authorName, String position, String email, String contactNumber, String titleOfBook, String topic, String description, String previousWork) {
        String registerAsAuthorURL = Constant.BASE_URL + "author/createAuthor";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerAsAuthorURL,
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
                                Toast.makeText(JoinAsAuthorActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(JoinAsAuthorActivity.this,DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle failure or error response
                                Toast.makeText(JoinAsAuthorActivity.this, "Failed to create bulk order: " + responseStatus, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException", e.toString());
                            Toast.makeText(JoinAsAuthorActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e("OnErrorResponse", error.toString());
                        Toast.makeText(JoinAsAuthorActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Set the parameters for the POST request
                Map<String, String> params = new HashMap<>();
                params.put("authorName", authorName);
                params.put("position", position);
                params.put("email", email);
                params.put("contactNumber", contactNumber);
                params.put("title", titleOfBook);
                params.put("topic", topic);
                params.put("description", description);
                params.put("previousWork", previousWork);
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