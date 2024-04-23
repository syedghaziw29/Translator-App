package com.example.chatbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class TranslationActivity extends AppCompatActivity {
    private Spinner fromSpinner,toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV;

    private MaterialButton translateBtn;
    private TextView translatedTV;
    String[] fromlanguages={"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali",
    "Catalan","Czech","Welsh","Hindi","Urdu"};
    String[] tolanguages={"to","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali",
            "Catalan","Czech","Welsh","Hindi","Urdu"};

    private static final int REQUEST_PERMISSION_CODE=1;
    int languageCode,fromLanguageCode,toLanguageCode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        fromSpinner=findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        sourceEdt=findViewById(R.id.idEdtSource);
        micIV=findViewById(R.id.idIVMic);
        translateBtn=findViewById(R.id.idBtnTranslate);
        translatedTV=findViewById(R.id.idTVTranslate);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode= getLanguageCode(fromlanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter =new ArrayAdapter(this, R.layout.spinner_item,fromlanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(tolanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter =new ArrayAdapter(this, R.layout.spinner_item,tolanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedTV.setText("");
                if(sourceEdt.getText().toString().isEmpty()){
                    Toast.makeText(TranslationActivity.this,"please enter text to translate",Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode==0) {
                    Toast.makeText(TranslationActivity.this,"please select source language",Toast.LENGTH_SHORT).show();
                }else if (toLanguageCode==0) {
                    Toast.makeText(TranslationActivity.this,"please select the language to make translation",Toast.LENGTH_SHORT).show();
                }else{
                    translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());

                }
            }
        });
        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text");
                try{
                startActivityForResult(i,REQUEST_PERMISSION_CODE);}
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(TranslationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();


                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE){

            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));

            }
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source){
        translatedTV.setText("Downloading Model..");
        FirebaseTranslatorOptions options=new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode).setTargetLanguage(toLanguageCode).build();

        FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTV.setText("Translating..");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TranslationActivity.this,"Fail to translate" +e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslationActivity.this,"Fail to download model" ,Toast.LENGTH_SHORT).show();
            }
        });


    }

    private int getLanguageCode(String language) {
        int languageCode=0;
        switch (language){
            case "English":
                languageCode= FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode= FirebaseTranslateLanguage.AF;
                break;

            case "Belarusian":
                languageCode= FirebaseTranslateLanguage.BE;
                break;
            case "Bengali":
                languageCode= FirebaseTranslateLanguage.BN;
                break;
            case "Catalan":
                languageCode= FirebaseTranslateLanguage.CA;
                break;
            case "Czech":
                languageCode= FirebaseTranslateLanguage.CS;
                break;
            case "Welsh":
                languageCode= FirebaseTranslateLanguage.CY;
                break;
            case "Hindi":
                languageCode= FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                languageCode= FirebaseTranslateLanguage.UR;
                break;
            default:
                languageCode=0;


        }
return languageCode;
    }
}