package com.example.planetzeproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planetzeproject.databinding.ActivityEcogaugeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class EcoTrackerActivity extends AppCompatActivity {
    public HashMap<String, String> spinnerData = new HashMap<>();
    public HashMap<String, String> textInputData = new HashMap<>();
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private TextView textSelectedDate;
    private String selectedDate;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecotracker);

        //navbar

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.tracker);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.tracker) {
                return true;
            } else if (id == R.id.gauge) {
                startActivity(new Intent(EcoTrackerActivity.this, EcoGaugeActivity.class));
                finish();
                return true;
            } else if (id == R.id.logout) {
                FirebaseAuth.getInstance().signOut();

                // Clear any locally stored user data (if necessary)
                SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to WelcomeActivity
                Intent intent = new Intent(EcoTrackerActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();

                return true;
            } else {
                return false;
            }
        });

        //initializing database stuff
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textSelectedDate = findViewById(R.id.text_selected_date);
        textSelectedDate.setOnClickListener((v -> openDatePicker()));

        // setting spinner list
        String[] transportOptions = {"Bus", "Train", "Subway"};
        String[] flightOptions = {"Short haul (<1,500km)", "Long haul(>1,500km)"};
        String[] foodOptions = {"Beef", "Pork", "Chicken", "Fish", "Plant-based"};
        String[] deviceOptions = {"Smartphone", "TV", "Laptop"};
        String[] purchaseOptions = {"Furniture", "Appliances"};
        String[] billOptions = {"Energy", "Gas", "Water"};

        Spinner spinnerTransportType = findViewById(R.id.spinner_transport_type);
        Spinner spinnerFlightType = findViewById(R.id.spinner_flight_type);
        Spinner spinnerFoodType = findViewById(R.id.spinner_food_type);
        Spinner spinnerElectronicDevice = findViewById(R.id.spinner_electronic_device);
        Spinner spinnerPurchaseType = findViewById(R.id.spinner_purchase_type);
        Spinner spinnerBillType = findViewById(R.id.spinner_bill_type);

        EditText inputKilometers = findViewById(R.id.input_kilometers);
        EditText inputTransportation = findViewById(R.id.input_transportation);
        EditText inputDistanceWalked = findViewById(R.id.input_distance_walked);
        EditText inputFlightsTaken = findViewById(R.id.input_flights_taken);
        EditText inputServings = findViewById(R.id.input_servings);
        EditText inputClothesPurchased = findViewById(R.id.input_clothes_purchased);
        EditText inputDevicesPurchased = findViewById(R.id.input_devices_purchased);
        EditText inputItemPurchased = findViewById(R.id.input_item_purchased);
        EditText inputBillAmount = findViewById(R.id.input_bill_amount);

        Button btnSubmit = findViewById(R.id.btn_submit);
        Button btnupdate = findViewById(R.id.btn_update);
        Button btnDelete = findViewById(R.id.btn_delete);

        createSpinner(spinnerTransportType, transportOptions);
        createSpinner(spinnerFlightType, flightOptions);
        createSpinner(spinnerFoodType, foodOptions);
        createSpinner(spinnerElectronicDevice, deviceOptions);
        createSpinner(spinnerPurchaseType, purchaseOptions);
        createSpinner(spinnerBillType, billOptions);

        setupSpinnerListener(spinnerTransportType, "Transport");
        setupSpinnerListener(spinnerFlightType, "Flight");
        setupSpinnerListener(spinnerFoodType, "Food");
        setupSpinnerListener(spinnerElectronicDevice, "Device");
        setupSpinnerListener(spinnerPurchaseType, "Purchase");
        setupSpinnerListener(spinnerBillType, "Bill");

        btnSubmit.setOnClickListener(v -> {
            textInputData.put("Kilometers Driven", inputKilometers.getText().toString().trim());
            textInputData.put("Transport Time", inputTransportation.getText().toString().trim());
            textInputData.put("Distance Walked", inputDistanceWalked.getText().toString().trim());
            textInputData.put("Flights Taken", inputFlightsTaken.getText().toString().trim());
            textInputData.put("Servings Consumed", inputServings.getText().toString().trim());
            textInputData.put("Clothes Purchased", inputClothesPurchased.getText().toString().trim());
            textInputData.put("Devices Purchased", inputDevicesPurchased.getText().toString().trim());
            textInputData.put("Items Purchased", inputItemPurchased.getText().toString().trim());
            textInputData.put("Bill Amount", inputBillAmount.getText().toString().trim());
            saveToFirebase();
        });

        btnupdate.setOnClickListener(v -> {
            retrieveFromFirebase();
        });

        btnDelete.setOnClickListener(v -> {
            deleteFromDatabase();
        });
    }

    public void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EcoTrackerActivity.this,
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    // Format the selected date
                    selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth1;
                    textSelectedDate.setText(selectedDate); // Update the TextView with the selected date
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void createSpinner(Spinner spinner, String[] options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void setupSpinnerListener(Spinner spinner, String type) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                spinnerData.put(type, selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerData.put(type, "None");
            }
        });
    }

    private void saveToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to login page
            Toast.makeText(EcoTrackerActivity.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            Intent intent = new Intent(EcoTrackerActivity.this, LoginActivity.class);
            startActivity(intent);
            return; // Stop execution if no user is logged in
        }
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(EcoTrackerActivity.this, "Please select a date to save data.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = currentUser.getUid(); // redirect null to login page later

        HashMap<String, Object> userEcoData = new HashMap<>();
        userEcoData.put("spinnerData", spinnerData);
        userEcoData.put("textInputData", textInputData);

        databaseReference.child("users").child(userID).child("ecoTrackerData").child(selectedDate).setValue(userEcoData)
                .addOnSuccessListener(aVoid -> {
                    //success message
                    Toast.makeText(EcoTrackerActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    retrieveFromFirebase();
                })
                .addOnFailureListener(e -> {
                    //or handle failure another way
                    Toast.makeText(EcoTrackerActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void retrieveFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to login page
            Toast.makeText(EcoTrackerActivity.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            Intent intent = new Intent(EcoTrackerActivity.this, LoginActivity.class);
            startActivity(intent);
            return; // Stop execution if no user is logged in
        }
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(EcoTrackerActivity.this, "Please select a date to delete data.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = currentUser.getUid(); // redirect null to login page later
        DatabaseReference userEcoDataRef = databaseReference.child("users").child(userID).child("ecoTrackerData").child(selectedDate);

        userEcoDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                    if (data != null) {
                        // Retrieve spinnerData and textInputData
                        spinnerData = (HashMap<String, String>) data.get("spinnerData");
                        textInputData = (HashMap<String, String>) data.get("textInputData");

                        if (spinnerData != null && textInputData != null) {
                            // Do something with the retrieved data
                            Toast.makeText(EcoTrackerActivity.this, "Data retrieved successfully!", Toast.LENGTH_SHORT).show();
                            performCalculations(spinnerData, textInputData);
                        } else {
                            Toast.makeText(EcoTrackerActivity.this, "Data missing for selected date.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    performCalculations();
                    Toast.makeText(EcoTrackerActivity.this, "No data found for the selected date. " + selectedDate, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                Toast.makeText(EcoTrackerActivity.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFromDatabase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to login page
            Toast.makeText(EcoTrackerActivity.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            Intent intent = new Intent(EcoTrackerActivity.this, LoginActivity.class);
            startActivity(intent);
            return; // Stop execution if no user is logged in
        }
        if (selectedDate == null || selectedDate.isEmpty()) {
            Toast.makeText(EcoTrackerActivity.this, "Please select a date to view data.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = currentUser.getUid(); // redirect null to login page later

        DatabaseReference userEcoDataRef = databaseReference.child("users").child(userID).child("ecoTrackerData").child(selectedDate);
        userEcoDataRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Data deleted successfully
                    Toast.makeText(EcoTrackerActivity.this, "Data deleted successfully!", Toast.LENGTH_SHORT).show();
                    performCalculations();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(EcoTrackerActivity.this, "Failed to delete data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void performCalculations(HashMap<String, String> spinnerData, HashMap<String, String> textInputData) {
        double kilometersDriven = Double.parseDouble(textInputData.get("Kilometers Driven"));
        double transportTime = Double.parseDouble(textInputData.get("Transport Time"));
        double distanceWalkedCycled = Double.parseDouble(textInputData.get("Distance Walked"));
        double flightsTaken = Double.parseDouble(textInputData.get("Flights Taken"));
        double servingsConsumed = Double.parseDouble(textInputData.get("Servings Consumed"));
        double newClothes = Double.parseDouble(textInputData.get("Clothes Purchased"));
        double newDevices = Double.parseDouble(textInputData.get("Devices Purchased"));
        double newItems = Double.parseDouble(textInputData.get("Items Purchased"));
        double billAmount = Double.parseDouble(textInputData.get("Bill Amount"));

        String flightType = spinnerData.get("Transport");
        String servingsType = spinnerData.get("Food");
        String devicesType = spinnerData.get("Device");
        String itemType = spinnerData.get("Purchase");
        String billType = spinnerData.get("Bill");


        double co2Transport = 0.0;

        if (transportTime < 1.0) {
            co2Transport += 573.0;
        } else if (transportTime >= 1.0 && transportTime <= 3.0) {
            co2Transport += 1911;
        } else if (transportTime >= 3.0 && transportTime <= 5.0) {
            co2Transport += 3822;
        } else if (transportTime >= 5.0 && transportTime <= 10.0) {
            co2Transport += 7166;
        } else if (transportTime > 10.0) {
            co2Transport += 9555;
        }
        if (Objects.equals(flightType, "Short haul (<1,500km)")) {
            if (flightsTaken >= 1.0 && flightsTaken <= 2.0) {
                co2Transport += 225;
            } else if (flightsTaken >= 3.0 && flightsTaken <= 5.0) {
                co2Transport += 600;
            } else if (flightsTaken >= 6.0 && flightsTaken <= 10.0) {
                co2Transport += 1200;
            } else if (flightsTaken > 10.0) {
                co2Transport += 1800;
            }
        } else {
            if (flightsTaken >= 1.0 && flightsTaken <= 2.0) {
                co2Transport += 825;
            } else if (flightsTaken >= 3.0 && flightsTaken <= 5.0) {
                co2Transport += 2200;
            } else if (flightsTaken >= 6.0 && flightsTaken <= 10.0) {
                co2Transport += 4400;
            } else if (flightsTaken > 10.0) {
                co2Transport += 6600;
            }
        }

        co2Transport += (distanceWalkedCycled * 0.063);
        co2Transport /= 365;
        co2Transport += kilometersDriven;

        double co2Food = 0.0;
        if (Objects.equals(servingsType, "Beef")) {
            co2Food = co2Food + (15 * servingsConsumed);
        } else if (Objects.equals(servingsType, "Pork")) {
            co2Food = co2Food + (10 * servingsConsumed);
        } else if (Objects.equals(servingsType, "Chicken")) {
            co2Food = co2Food + (9 * servingsConsumed);
        } else if (Objects.equals(servingsType, "Fish")) {
            co2Food = co2Food + (8 * servingsConsumed);
        } else {
            co2Food = co2Food + (5 * servingsConsumed);
        }

        double co2Consumption = 0.0;
        co2Consumption = (newClothes * 30) + (newDevices * 300);
        if (Objects.equals(billType, "Energy")) {
            co2Consumption += (billAmount * 7.08);
        } else if (Objects.equals(billType, "Gas")) {
            co2Consumption += (billAmount * 1.27);
        } else {
            co2Consumption += (billAmount * 0.17);
        }
        co2Consumption += (newItems * 10);

        co2Transport = Math.round(co2Transport * 100.0) / 100.0;
        co2Food = Math.round(co2Food * 100.0) / 100.0;
        co2Consumption = Math.round(co2Consumption * 100.0) / 100.0;
        double totalCo2 = co2Transport + co2Food + co2Consumption;
        totalCo2 = Math.round(totalCo2 * 100.0) / 100.0;

        HashMap<String, Object> userCo2Data = new HashMap<>();
        userCo2Data.put("co2Transport", co2Transport);
        userCo2Data.put("co2Food", co2Food);
        userCo2Data.put("co2Consumption", co2Consumption);
        userCo2Data.put("totalCo2", totalCo2);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();
        databaseReference.child("users").child(userID).child("ecoTrackerData").child(selectedDate).updateChildren(userCo2Data);

        TextView emissionsTextView = findViewById(R.id.textViewTotalEmissions);
        emissionsTextView.setText("Total CO₂e: " + totalCo2 + " kg");
        TextView TransportationTextView = findViewById(R.id.textTransportationEmissions);
        TransportationTextView.setText("Transportation CO₂e: " + co2Transport + " kg");
        TextView FoodTextView = findViewById(R.id.textFoodConsumption);
        FoodTextView.setText("Food CO₂e: " + co2Food + "kg");
        TextView ConsumptionTextView = findViewById(R.id.textShoppingConsumption);
        ConsumptionTextView.setText("Consumption CO₂e: " + co2Consumption + " kg");

    }
    public void performCalculations() {
        TextView emissionsTextView = findViewById(R.id.textViewTotalEmissions);
        emissionsTextView.setText("Total CO₂e: 0.0 kg");
        TextView TransportationTextView = findViewById(R.id.textTransportationEmissions);
        TransportationTextView.setText("Transportation CO₂e: 0.0 kg");
        TextView FoodTextView = findViewById(R.id.textFoodConsumption);
        FoodTextView.setText("Food CO₂e: 0.0 kg");
        TextView ConsumptionTextView = findViewById(R.id.textShoppingConsumption);
        ConsumptionTextView.setText("Consumption CO₂e: 0.0 kg");
    }

}

