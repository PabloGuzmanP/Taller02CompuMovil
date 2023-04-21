package com.example.taller_02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button buttonCamera;
    Button buttonMapas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCamera = (Button) findViewById(R.id.botoncamara);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openNewActivity();
            }
        });
        // Segundo botón agregado
        buttonMapas = (Button) findViewById(R.id.botonmapas);
        buttonMapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewActivity2();
            }
        });
    }
    public void openNewActivity(){
        Intent intent = new Intent(this, Camara.class);
        startActivity(intent);
    }
    public void openNewActivity2(){
        Intent intent = new Intent(this, Mapa.class);
        startActivity(intent);
    }
}