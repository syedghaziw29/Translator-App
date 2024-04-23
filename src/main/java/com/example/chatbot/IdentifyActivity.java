package com.example.chatbot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import java.util.Locale;
public class IdentifyActivity extends AppCompatActivity {
    private EditText edtLanguage;
    private TextView languageCodeTV;
    private TextView languageNameTV;
    private Button detectLanguageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
// Initializing variables.
        edtLanguage = findViewById(R.id.idEdtLanguage);
        languageCodeTV = findViewById(R.id.idTVDetectedLanguageCode);
        languageNameTV = findViewById(R.id.idTVDetectedLanguageName);
        detectLanguageBtn = findViewById(R.id.idBtnDetectLanguage);

        // Adding onClickListener for the button.
        detectLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting string from our EditText.
                String edtString = edtLanguage.getText().toString();
                // Calling method to detect language.
                detectLanguage(edtString);
            }
        });
    }

    private void detectLanguage(String string) {
        // Initializing Firebase language detection.
        FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        // Adding method to detect language using identifyLanguage method.
        languageIdentifier.identifyLanguage(string).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String languageCode) {
                // Setting language code to our TextView.
                languageCodeTV.setText(languageCode);

                // Using FirebaseLanguageId to get the full language name.
                String languageName = getLanguageName(languageCode);
                // Setting full language name to our TextView.
                languageNameTV.setText(languageName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handling error and displaying a toast message.
                Toast.makeText(IdentifyActivity.this, "Fail to detect language: \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getLanguageName(String languageCode) {
        // Using Locale class to get the display name of the language.
        Locale locale = new Locale(languageCode);
        return locale.getDisplayName();
    }
}