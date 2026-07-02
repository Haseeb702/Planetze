package com.example.planetzeproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class QuestionnaireActivity extends AppCompatActivity {

    protected String selectedCategory, selectedQuestion;
    String emptyAnswer = "-"; // Will be overwritten during onCreate()


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire);
        emptyAnswer = getResources().getString(R.string.empty_answer);

        // Set up maps (idk, it works?)

        String[] categoryQuestions = getResources().getStringArray(R.array.questionCategory);

        String[] locationQuestions = getResources().getStringArray(R.array.locationQuestions);
        String[] transportationQuestions = getResources().getStringArray(R.array.transportationQuestions);
        String[] foodQuestions = getResources().getStringArray(R.array.foodQuestions);
        String[] housingQuestions = getResources().getStringArray(R.array.housingQuestions);
        String[] consumptionQuestions = getResources().getStringArray(R.array.consumptionQuestions);

        final Map<String, String[]> categoryMap = new HashMap<>();
        categoryMap.put(categoryQuestions[0], locationQuestions);
        categoryMap.put(categoryQuestions[1], transportationQuestions);
        categoryMap.put(categoryQuestions[2], foodQuestions);
        categoryMap.put(categoryQuestions[3], housingQuestions);
        categoryMap.put(categoryQuestions[4], consumptionQuestions);

        final Map<String, String[]> locationQuestionsMap = new HashMap<>();
        locationQuestionsMap.put(locationQuestions[0], getResources().getStringArray(R.array.countryList));

        final Map<String, String[]> transportationQuestionsMap = new HashMap<>();
        transportationQuestionsMap.put(transportationQuestions[0], getResources().getStringArray(R.array.userOwnsCar));
        transportationQuestionsMap.put(transportationQuestions[1], getResources().getStringArray(R.array.vehicleFuelType));
        transportationQuestionsMap.put(transportationQuestions[2], getResources().getStringArray(R.array.distanceDriven));
        transportationQuestionsMap.put(transportationQuestions[3], getResources().getStringArray(R.array.publicTransportationFrequency));
        transportationQuestionsMap.put(transportationQuestions[4], getResources().getStringArray(R.array.publicTransportationTime));
        transportationQuestionsMap.put(transportationQuestions[5], getResources().getStringArray(R.array.shortFlightFrequency));
        transportationQuestionsMap.put(transportationQuestions[6], getResources().getStringArray(R.array.longFlightFrequency));

        final Map<String, String[]> foodQuestionsMap = new HashMap<>();
        foodQuestionsMap.put(foodQuestions[0], getResources().getStringArray(R.array.dietType));
        foodQuestionsMap.put(foodQuestions[1], getResources().getStringArray(R.array.meatFrequency));
        foodQuestionsMap.put(foodQuestions[2], getResources().getStringArray(R.array.meatFrequency));
        foodQuestionsMap.put(foodQuestions[3], getResources().getStringArray(R.array.meatFrequency));
        foodQuestionsMap.put(foodQuestions[4], getResources().getStringArray(R.array.meatFrequency));
        foodQuestionsMap.put(foodQuestions[5], getResources().getStringArray(R.array.foodWasteFrequency));

        final Map<String, String[]> housingQuestionsMap = new HashMap<>();
        housingQuestionsMap.put(housingQuestions[0], getResources().getStringArray(R.array.housingType));
        housingQuestionsMap.put(housingQuestions[1], getResources().getStringArray(R.array.peopleInHousehold));
        housingQuestionsMap.put(housingQuestions[2], getResources().getStringArray(R.array.housingSize));
        housingQuestionsMap.put(housingQuestions[3], getResources().getStringArray(R.array.housingHeating));
        housingQuestionsMap.put(housingQuestions[4], getResources().getStringArray(R.array.monthlyElectricityBill));
        housingQuestionsMap.put(housingQuestions[5], getResources().getStringArray(R.array.waterHeating));
        housingQuestionsMap.put(housingQuestions[6], getResources().getStringArray(R.array.renewableEnergyUse));

        final Map<String, String[]> consumptionQuestionsMap = new HashMap<>();
        consumptionQuestionsMap.put(consumptionQuestions[0], getResources().getStringArray(R.array.newClothesFrequency));
        consumptionQuestionsMap.put(consumptionQuestions[1], getResources().getStringArray(R.array.ecoFriendlyProductsFrequency));
        consumptionQuestionsMap.put(consumptionQuestions[2], getResources().getStringArray(R.array.electronicsFrequency));
        consumptionQuestionsMap.put(consumptionQuestions[3], getResources().getStringArray(R.array.recyclingFrequency));

        final Map<String, Map<String, String[]>> stringToQuestions = new HashMap<>();
        stringToQuestions.put(categoryQuestions[0], locationQuestionsMap);
        stringToQuestions.put(categoryQuestions[1], transportationQuestionsMap);
        stringToQuestions.put(categoryQuestions[2], foodQuestionsMap);
        stringToQuestions.put(categoryQuestions[3], housingQuestionsMap);
        stringToQuestions.put(categoryQuestions[4], consumptionQuestionsMap);


        // Make button go to EcoTracker
        Button button = findViewById(R.id.questionnaireSubmit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnaireActivity.this, EcoTrackerActivity.class);
                startActivity(intent);
            }
        });


        // Get the spinner from the layout
        Spinner categorySelector = findViewById(R.id.categorySelector);
        Spinner questionSelector = findViewById(R.id.questionSelector);
        Spinner answerSelector = findViewById(R.id.answerSelector);




        // Create an ArrayAdapter using the category array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(QuestionnaireActivity.this, android.R.layout.simple_spinner_item, categoryQuestions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        categorySelector.setAdapter(adapter);

        // Default category and question
        selectedCategory = categoryQuestions[0];
        selectedQuestion = Objects.requireNonNull(categoryMap.get(categoryQuestions[0]))[0];

        // Set an OnItemSelectedListener to detect user selection
        categorySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected category from the spinner
                selectedCategory = parentView.getItemAtPosition(position).toString();

                String[] newQuestions = categoryMap.get(selectedCategory);

                // Populate question spinner
                if (newQuestions != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(QuestionnaireActivity.this, android.R.layout.simple_spinner_item, newQuestions);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    questionSelector.setAdapter(adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] defaultQuestions = categoryMap.get(categoryQuestions[0]);

        assert defaultQuestions != null;
        adapter = new ArrayAdapter<>(QuestionnaireActivity.this, android.R.layout.simple_spinner_item, defaultQuestions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionSelector.setAdapter(adapter);

        questionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected question from the spinner
                selectedQuestion = parentView.getItemAtPosition(position).toString();

                TextView questionText = findViewById(R.id.questionnaireQuestionText);
                questionText.setText(selectedQuestion);

                Map<String, String[]> currentQuestionMap = stringToQuestions.get(selectedCategory);

                assert currentQuestionMap != null;
                String[] newAnswers = currentQuestionMap.get(selectedQuestion);

                // Populate answer spinner
                if (newAnswers != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(QuestionnaireActivity.this, android.R.layout.simple_spinner_item, newAnswers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    answerSelector.setAdapter(adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected
            }
        });

        String[] defaultAnswers = getResources().getStringArray(R.array.countryList);
        adapter = new ArrayAdapter<>(QuestionnaireActivity.this, android.R.layout.simple_spinner_item, defaultAnswers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        answerSelector.setAdapter(adapter);

        answerSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected answer from the spinner
                String selectedAnswer = parentView.getItemAtPosition(position).toString();

                // Save the selected answer
                QuestionnaireActivity.this.saveAnswer(db, user, selectedQuestion, selectedAnswer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveAnswer(FirebaseDatabase db, FirebaseUser user, String question, String answer){
        String userFormatted;
        if (user != null) {
            userFormatted = user.getUid().replace("/", "%2F");
        }
        else {
            userFormatted = "nulluser";
        }
        String questionFormatted = QuestionnaireActivity.this.formatString(question);
        String answerFormatted = QuestionnaireActivity.this.formatString(answer);
        DatabaseReference uref = db.getReference("users");
        uref.child(userFormatted).child("questionnaireData").child(questionFormatted).setValue(answerFormatted);
    }

    private String formatString(String string){
        String formattedString;
        formattedString = string.replace("/", "%2F");
        formattedString = formattedString.replace(".", "%2E");
        return formattedString;
    }
}