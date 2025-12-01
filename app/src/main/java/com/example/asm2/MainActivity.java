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

        // Xin quyền thông báo (Bắt buộc cho Android 13 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // --- 1. ÁNH XẠ VIEW (Tìm các nút theo ID) ---
        btnExpense = findViewById(R.id.btnExpense);
        btnBudget = findViewById(R.id.btnBudget);
        btnStatistic = findViewById(R.id.btnStatistic);
        btnOffer = findViewById(R.id.btnOffer);
        btnEvent = findViewById(R.id.btnEvent); // Nút mới: Sự kiện
        btnNotification = findViewById(R.id.btnNotification);
        btnTestNotify = findViewById(R.id.btnTestNotify);

        // --- 2. GẮN SỰ KIỆN CHUYỂN MÀN HÌNH ---


        // Thiết lập ngân sách
        btnBudget.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BudgetActivity.class)));


        // Săn ưu đãi
        btnOffer.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OfferActivity.class)));


    }


}