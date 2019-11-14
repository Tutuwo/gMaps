package com.example.gmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity {


    LinearLayout btn1, btn2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        btn1 = findViewById(R.id.registo_o);
        btn2 = findViewById(R.id.listagem_o);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

    }
}
