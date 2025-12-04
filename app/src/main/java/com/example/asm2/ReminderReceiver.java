package com.example.asm2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast; // Thư viện Toast

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // --- 1. HIỆN TOAST ĐỂ KIỂM TRA (DEBUG) ---
        // Nếu thấy dòng này hiện lên màn hình nghĩa là báo thức ĐÃ CHẠY
        Toast.makeText(context, "⏰ Receiver đã nhận lệnh báo thức!", Toast.LENGTH_LONG).show();
        // -----------------------------------------

        createNotificationChannel(context);

        // 2. Lấy dữ liệu nội dung được gửi sang
        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");

        // Nội dung mặc định nếu null
        if (title == null) title = "Thông báo ASM2";
        if (content == null) content = "Đã đến giờ hẹn!";

        // 3. Sự kiện khi bấm vào thông báo -> Mở MainActivity
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        // 4. Cấu hình giao diện thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "EVENT_CHANNEL")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ưu tiên cao
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 5. Kiểm tra quyền và hiển thị
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Dùng ID theo thời gian để không bị đè thông báo cũ
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
        } else {
            Toast.makeText(context, "Lỗi: App chưa được cấp quyền Thông báo!", Toast.LENGTH_LONG).show();
        }
    }

    // Tạo kênh thông báo (Bắt buộc cho Android 8.0+)
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Thông báo Sự kiện";
            String description = "Kênh nhắc nhở sự kiện";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("EVENT_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}