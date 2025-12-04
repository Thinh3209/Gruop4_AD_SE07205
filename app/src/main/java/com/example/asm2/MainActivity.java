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

        // Theo dõi chi phí
        btnExpense.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExpenseActivity.class)));

        // Thiết lập ngân sách
        btnBudget.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BudgetActivity.class)));

        // Xem báo cáo
        btnStatistic.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StatisticActivity.class)));

        // Săn ưu đãi
        btnOffer.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OfferActivity.class)));

        // Nhắc nhở sự kiện (MỚI)
        btnEvent.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EventActivity.class)));

        // --- 3. CÁC CHỨC NĂNG THÔNG BÁO ---

        // Bật nhắc nhở cố định 20:00 hàng ngày
        btnNotification.setOnClickListener(v -> scheduleDailyReminder());

        // Test thông báo nhanh (10 giây sau nổ)
        btnTestNotify.setOnClickListener(v -> scheduleTestNotification());
    }

    // Hàm hẹn giờ 20:00 hàng ngày
    private void scheduleDailyReminder() {
        Intent intent = new Intent(MainActivity.this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20); // 20 giờ
        calendar.set(Calendar.MINUTE, 0);       // 0 phút
        calendar.set(Calendar.SECOND, 0);

        // Nếu đã qua 20h hôm nay thì hẹn sang ngày mai
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Lặp lại hàng ngày
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );

        Toast.makeText(this, "Đã bật nhắc nhở lúc 20:00 hàng ngày!", Toast.LENGTH_SHORT).show();
    }

    // Hàm test nhanh (10 giây sau)
    private void scheduleTestNotification() {
       Intent intent = new Intent(MainActivity.this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10); // Cộng thêm 10 giây

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(this, "Đã đặt! Thoát app ngay và đợi 10 giây...", Toast.LENGTH_LONG).show();
    }
}