package com.example.planetzeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class was intended to handle the habit suggestion portion of Eco Tracker, but is unfinished
 */

public class EcoTrackerB extends AppCompatActivity {
    String selectedDate; //REMEMBER TO MAKE THIS HYPHENS AND NOT SLASHES
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecotracker);
    }

    private void saveAnswerEcoB (String selectedQuestion, String selectedAnswer){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String Uid = user.getUid();
            DatabaseReference uref = db.getReference("users");
            uref.child(Uid).child("ecoTrackerBData").child(selectedDate)
                    .child(EcoTrackerB.this.formatString(selectedQuestion))
                    .setValue(EcoTrackerB.this.formatString(selectedAnswer));
        } else {
            // User is not logged in, redirect to login page
            Toast.makeText(EcoTrackerB.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            Intent intent = new Intent(EcoTrackerB.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void loadAnswers(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is not logged in, redirect to login page
            Toast.makeText(EcoTrackerB.this, "Please log in first.", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            Intent intent = new Intent(EcoTrackerB.this, LoginActivity.class);
            startActivity(intent);
            return; // Stop execution if no user is logged in
        }
        String Uid = user.getUid();
        DatabaseReference ecoDataBRef = db.getReference("users").child(Uid).child(
                "ecoTrackerBData");
        HashMap<String, HashMap<String, String>> dateToMap = new HashMap<>();
        ecoDataBRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                HashMap<String, String>  QNA;
                for (DataSnapshot date: dataSnapshot.getChildren()){
                    String dateKey = date.getKey();
                    QNA = new HashMap<>();
                    for (DataSnapshot question: date.getChildren()){
                        String questionKey = question.getKey();
                        String answer = question.getValue(String.class);
                        if (questionKey == null || answer == null){
                            continue;
                        }
                        QNA.put(EcoTrackerB.this.decodeString(questionKey),
                                EcoTrackerB.this.decodeString(answer));
                    }
                    dateToMap.put(dateKey, QNA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){
                // Handle errors
                Toast.makeText(EcoTrackerB.this, "Failed to retrieve data: "
                        + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateEcoBFootprint(HashMap<String, String> QNA){
        return;
    }

    private String formatString (String string){
        String formattedString;
        formattedString = string.replace("/", "%2F");
        formattedString = formattedString.replace(".", "%2E");
        return formattedString;
    }

    private String decodeString (String string){
        String decodedString;
        decodedString = string.replace("%2F", "/");
        decodedString = decodedString.replace("%2E", ".");
        return decodedString;
    }
}
