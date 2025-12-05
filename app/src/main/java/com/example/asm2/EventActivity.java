package com.example.asm2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity {

    EditText edtContent;
    Button btnPick;
    ListView lvEvents;
    ArrayList<EventItem> eventList;
    EventAdapter adapter;

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        edtContent = findViewById(R.id.edtEventContent);
        btnPick = findViewById(R.id.btnPickDateTime);
        lvEvents = findViewById(R.id.lvEvents);

        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);
        lvEvents.setAdapter(adapter);

        calendar = Calendar.getInstance();

        btnPick.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        String content = edtContent.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung sự kiện!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chọn Ngày
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            showTimePicker(); // Chọn xong ngày thì mở chọn giờ
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void showTimePicker() {
        // Chọn Giờ
        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            // Kiểm tra nếu chọn thời gian trong quá khứ
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(this, "Vui lòng chọn thời gian trong tương lai!", Toast.LENGTH_SHORT).show();
                return;
            }

            addEventAndSchedule();

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePicker.show();
    }

    private void addEventAndSchedule() {
        String content = edtContent.getText().toString();

        // Format ngày giờ hiển thị
        String dateStr = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
        String timeStr = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        // Thêm vào list
        eventList.add(new EventItem(content, dateStr, timeStr));
        adapter.notifyDataSetChanged();

        // Đặt báo thức
        setAlarm(content, calendar.getTimeInMillis());

        Toast.makeText(this, "Đã đặt nhắc nhở lúc " + timeStr + "!", Toast.LENGTH_LONG).show();
        edtContent.setText("");
    }

    // --- HÀM QUAN TRỌNG NHẤT: ĐẶT BÁO THỨC ---
    private void setAlarm(String content, long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);

        intent.putExtra("TITLE", "Sự kiện sắp đến! 📅");
        intent.putExtra("CONTENT", content);

        // Dùng ID ngẫu nhiên để không bị trùng
        int uniqueId = (int) System.currentTimeMillis();

        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uniqueId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // setExactAndAllowWhileIdle: Đảm bảo nổ chuông kể cả khi tắt màn hình (Android 6.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }

    // Adapter hiển thị Listview
    class EventAdapter extends ArrayAdapter<EventItem> {
        public EventAdapter(@NonNull Context context, ArrayList<EventItem> list) {
            super(context, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_event_item, parent, false);
            }
            EventItem item = getItem(position);
            TextView title = convertView.findViewById(R.id.txtEventTitle);
            TextView date = convertView.findViewById(R.id.txtEventDate);
            TextView time = convertView.findViewById(R.id.txtEventTime);

            title.setText(item.getTitle());
            date.setText(item.getDate());
            time.setText(item.getTime());
            return convertView;
        }
    }
}
