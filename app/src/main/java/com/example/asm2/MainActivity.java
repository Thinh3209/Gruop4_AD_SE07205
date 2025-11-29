package com.example.asm2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Khai báo toàn bộ các nút chức năng
    Button btnExpense, btnBudget, btnStatistic, btnOffer, btnEvent, btnNotification, btnTestNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }


        btnExpense = findViewById(R.id.btnExpense);
        btnBudget = findViewById(R.id.btnBudget);
        btnStatistic = findViewById(R.id.btnStatistic);
        btnOffer = findViewById(R.id.btnOffer);
        btnEvent = findViewById(R.id.btnEvent);
        btnNotification = findViewById(R.id.btnNotification);
        btnTestNotify = findViewById(R.id.btnTestNotify);

    }
}