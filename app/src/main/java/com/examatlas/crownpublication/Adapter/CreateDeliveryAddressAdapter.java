package com.examatlas.crownpublication.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.CreateDeliveryAddressActivity;
import com.examatlas.crownpublication.R;
import com.examatlas.crownpublication.Models.CreateDeliveryAddressModel;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateDeliveryAddressAdapter extends RecyclerView.Adapter<CreateDeliveryAddressAdapter.ViewHolder> {

    Context context;
    ArrayList<CreateDeliveryAddressModel> createDeliveryAddressModelArrayList;
    public int selectedPosition = 0; // Variable to track the selected position
    private final String[] threeDotsArray = {"Edit", "Delete"};
    SessionManager sessionManager;
    String authToken;
    Dialog progressDialog;
    public CreateDeliveryAddressAdapter(Context context, ArrayList<CreateDeliveryAddressModel> createDeliveryAddressModelArrayList) {
        this.context = context;
        this.createDeliveryAddressModelArrayList = createDeliveryAddressModelArrayList;
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_address_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CreateDeliveryAddressModel currentAddress = createDeliveryAddressModelArrayList.get(position);
        String fullNameSTR = createDeliveryAddressModelArrayList.get(position).getFirstName() + " " + createDeliveryAddressModelArrayList.get(position).getLastName();
        String fullAddressSTR = createDeliveryAddressModelArrayList.get(position).getHouseNoOrApartmentNo() + ", " +
                createDeliveryAddressModelArrayList.get(position).getStreetAddress() + ", " +
                createDeliveryAddressModelArrayList.get(position).getTownCity() + ", " +
                createDeliveryAddressModelArrayList.get(position).getState() + ", " +
                createDeliveryAddressModelArrayList.get(position).getPinCode() + ", " +
                createDeliveryAddressModelArrayList.get(position).getCountryName() + ", " +
                createDeliveryAddressModelArrayList.get(position).getPhone() + ", " +
                createDeliveryAddressModelArrayList.get(position).getEmailAddress() + ".";

        holder.fullName.setText(fullNameSTR);
        holder.fullAddress.setText(fullAddressSTR);

        if (createDeliveryAddressModelArrayList.get(position).getIsDefault().equals("true")){
            holder.radioButton.setChecked(true);
        }else {
            holder.radioButton.setChecked(false);
        }

        // Set the RadioButton state based on the selected position
        holder.radioButton.setChecked(position == selectedPosition);

        // Set OnClickListener for the RadioButton
        holder.radioButton.setOnClickListener(v -> {

            // Update selected position
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged(); // Refresh the RecyclerView
        });
        holder.threeDotsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showThreeDotsOptions(currentAddress, position);
            }
        });
    }
    private void showThreeDotsOptions(CreateDeliveryAddressModel currentAddress, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(null)
                .setItems(threeDotsArray, (dialog, which) -> {
                    String selectedItems = threeDotsArray[which];
                    choseItems(currentAddress, selectedItems, position);
                });
        builder.create().show();
    }
    private void choseItems(CreateDeliveryAddressModel currentAddress, String selectedItems, int position) {
        if (selectedItems.equals("Edit")) {
            openEditAddressDialog(position,currentAddress);
        } else if (selectedItems.equals("Delete")) {
            quitDialog(position,currentAddress);
        }
    }
    private void quitDialog(int position, CreateDeliveryAddressModel currentAddress) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deleteBlog(position,currentAddress))
                .show();
    }
    private void deleteBlog(int position, CreateDeliveryAddressModel currentAddress) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_bar_drawer);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        progressDialog.show();
        String deleteURL = Constant.BASE_URL + "address/delete/" + currentAddress.getAddressId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if (status) {
                                Toast.makeText(context, "Address Deleted Successfully", Toast.LENGTH_SHORT).show();
                                createDeliveryAddressModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("BlogFetchError", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    EditText firstNameEditText,lastNameEditText,houseNoOrApartmentNoEditText,streetAddressEditText,townCityEditText,stateEditText,pinCodeEditText,countryNameEditText,phoneEditText,emailAddressEditText;
    Button saveAndContinueBtn;
    ImageView crossBtn;
    String [] addressTypeList = {"Select Address Type", "HOME","OFFICE"};
    Spinner addressTypeSpinner;
    String addressTypeString = "";
    private void openEditAddressDialog(int position, CreateDeliveryAddressModel selectedAddress) {
        Dialog billingAddressInputDialogBox = new Dialog(context);
        billingAddressInputDialogBox.setContentView(R.layout.delivery_address_input_layout);

        addressTypeSpinner = billingAddressInputDialogBox.findViewById(R.id.addressTypeSpinner);

        firstNameEditText = billingAddressInputDialogBox.findViewById(R.id.firstNameEditText);
        lastNameEditText = billingAddressInputDialogBox.findViewById(R.id.lastNameEditText);
        houseNoOrApartmentNoEditText = billingAddressInputDialogBox.findViewById(R.id.houseNumberEditText);
        streetAddressEditText = billingAddressInputDialogBox.findViewById(R.id.streetAddressEditText);
        townCityEditText = billingAddressInputDialogBox.findViewById(R.id.townCityEditText);
        stateEditText = billingAddressInputDialogBox.findViewById(R.id.stateEditText);
        pinCodeEditText = billingAddressInputDialogBox.findViewById(R.id.pinCodeEditText);
        countryNameEditText = billingAddressInputDialogBox.findViewById(R.id.countryNameEditText);
        phoneEditText = billingAddressInputDialogBox.findViewById(R.id.phoneEditText);
        emailAddressEditText = billingAddressInputDialogBox.findViewById(R.id.emailAddressEditText);

        saveAndContinueBtn = billingAddressInputDialogBox.findViewById(R.id.saveAndContinueBtn);
        crossBtn = billingAddressInputDialogBox.findViewById(R.id.crossBtn);

        // Create an ArrayAdapter to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, addressTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressTypeSpinner.setAdapter(adapter);

        addressTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAddressType = addressTypeList[i];
                if (selectedAddressType.equals("HOME")){
                    addressTypeString = "HOME";
                } else if (selectedAddressType.equals("OFFICE")) {
                    addressTypeString = "OFFICE";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        firstNameEditText.setText(selectedAddress.getFirstName());
        lastNameEditText.setText(selectedAddress.getLastName());
        houseNoOrApartmentNoEditText.setText(selectedAddress.getHouseNoOrApartmentNo());
        streetAddressEditText.setText(selectedAddress.getStreetAddress());
        townCityEditText.setText(selectedAddress.getTownCity());
        stateEditText.setText(selectedAddress.getState());
        pinCodeEditText.setText(selectedAddress.getPinCode());
        countryNameEditText.setText(selectedAddress.getCountryName());
        phoneEditText.setText(selectedAddress.getPhone());
        emailAddressEditText.setText(selectedAddress.getEmailAddress());
        saveAndContinueBtn.setText("Update");

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingAddressInputDialogBox.dismiss();
            }
        });

        saveAndContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress;

                firstName = firstNameEditText.getText().toString().trim();
                lastName = lastNameEditText.getText().toString().trim();
                houseNoOrApartmentNo = houseNoOrApartmentNoEditText.getText().toString().trim();
                streetAddress = streetAddressEditText.getText().toString().trim();
                townCity = townCityEditText.getText().toString().trim();
                state = stateEditText.getText().toString().trim();
                pinCode = pinCodeEditText.getText().toString().trim();
                countryName = countryNameEditText.getText().toString().trim();
                phone = phoneEditText.getText().toString().trim();
                emailAddress = emailAddressEditText.getText().toString().trim();

                // Check if all the required fields are filled
                if (firstName.isEmpty()) {
                    firstNameEditText.setError("First name is required");
                    return;
                }
                if (lastName.isEmpty()) {
                    lastNameEditText.setError("Last name is required");
                    return;
                }
                if (houseNoOrApartmentNo.isEmpty()) {
                    houseNoOrApartmentNoEditText.setError("House/Apartment No is required");
                    return;
                }
                if (streetAddress.isEmpty()) {
                    streetAddressEditText.setError("Street address is required");
                    return;
                }
                if (townCity.isEmpty()) {
                    townCityEditText.setError("Town/City is required");
                    return;
                }
                if (state.isEmpty()) {
                    stateEditText.setError("State is required");
                    return;
                }
                if (pinCode.isEmpty()) {
                    pinCodeEditText.setError("Pin code is required");
                    return;
                }
                if (countryName.isEmpty()) {
                    countryNameEditText.setError("Country name is required");
                    return;
                }
                if (phone.isEmpty()) {
                    phoneEditText.setError("Phone number is required");
                    return;
                }
                if (emailAddress.isEmpty()) {
                    emailAddressEditText.setError("Email address is required");
                    return;
                }

                // Check if address type is selected
                if (addressTypeString.isEmpty() || addressTypeString.equals("Select Address Type")) {
                    Toast.makeText(context, "Please Select Address Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateBillingAddress(addressTypeString, firstName, lastName, houseNoOrApartmentNo, streetAddress, townCity, state, pinCode, countryName, phone, emailAddress, billingAddressInputDialogBox, selectedAddress.getAddressId());

            }
        });

        billingAddressInputDialogBox.show();
        WindowManager.LayoutParams params = billingAddressInputDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;


        // Set the window attributes
        billingAddressInputDialogBox.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) billingAddressInputDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(30, 50, 30, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        billingAddressInputDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        billingAddressInputDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        billingAddressInputDialogBox.setCanceledOnTouchOutside(false);

    }
    private void updateBillingAddress(String addressTypeString,String firstName, String lastName, String houseNoOrApartmentNo, String streetAddress, String townCity, String state, String pinCode, String countryName, String phone, String emailAddress, Dialog billingAddressInputDialogBox, String billingId) {

        String createBillingURL = Constant.BASE_URL + "address/update/" + billingId;

        JSONObject billingDetailsObject = new JSONObject();
        try {
            billingDetailsObject.put("addressType", addressTypeString);
            billingDetailsObject.put("firstName", firstName);
            billingDetailsObject.put("lastName", lastName);
            billingDetailsObject.put("apartment", houseNoOrApartmentNo);
            billingDetailsObject.put("streetAddress", streetAddress);
            billingDetailsObject.put("city", townCity);
            billingDetailsObject.put("state", state);
            billingDetailsObject.put("pinCode", pinCode);
            billingDetailsObject.put("country", countryName);
            billingDetailsObject.put("phone", phone);
            billingDetailsObject.put("email", emailAddress);
        }
        catch (JSONException e){
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, createBillingURL, billingDetailsObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            String message = response.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                            if (status.equals("true")) {
                                ((CreateDeliveryAddressActivity) context).getBillingAddress();
                                billingAddressInputDialogBox.dismiss();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                billingAddressInputDialogBox.dismiss();
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    public int getItemCount() {
        return createDeliveryAddressModelArrayList.size();
    }
    public CreateDeliveryAddressModel getSelectedAddress() {
        if (selectedPosition != -1) {
            return createDeliveryAddressModelArrayList.get(selectedPosition);
        }
        return createDeliveryAddressModelArrayList.get(0); // No address selected
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, fullAddress;
        RadioButton radioButton;
        ImageView threeDotsBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.addressNameTxt);
            fullAddress = itemView.findViewById(R.id.addressFullTxt);
            radioButton = itemView.findViewById(R.id.radioButton);
            threeDotsBtn = itemView.findViewById(R.id.threeDotsBtn);
        }
    }
}
