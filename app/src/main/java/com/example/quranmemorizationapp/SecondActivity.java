package com.example.quranmemorizationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    EditText etFrom, etTo, etRepeat;
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etRepeat = findViewById(R.id.etRepeat);
        btnPlay = findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(intent);


                String from = etFrom.getText().toString();
                String to = etTo.getText().toString();
                String repeat = etRepeat.getText().toString();

                Toast.makeText(SecondActivity.this,
                        "من " + from + " إلى " + to + " تكرار " + repeat,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}