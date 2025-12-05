package com.example.asm2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExpenseActivity extends AppCompatActivity {

    EditText edtName, edtQuantity, edtPrice;
    Spinner spinnerCategory, spinnerFrequency;
    CheckBox cbRecurring;
    Button btnAdd;
    ListView listView;
    TextView txtTotalQuantity, txtTotalPrice, txtBudget, txtRemaining;

    ArrayList<ExpenseItem> expenses;
    ArrayList<String> expenseStrings;
    ArrayAdapter<String> adapter;

    double budget = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Ánh xạ View
        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtPrice = findViewById(R.id.edtPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        cbRecurring = findViewById(R.id.cbRecurring);
        spinnerFrequency = findViewById(R.id.spinnerFrequency);
        btnAdd = findViewById(R.id.btnAdd);
        listView = findViewById(R.id.listView);
        txtTotalQuantity = findViewById(R.id.txtTotalQuantity);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtBudget = findViewById(R.id.txtBudget);
        txtRemaining = findViewById(R.id.txtRemaining);

        String[] categories = {"Ăn uống", "Đi lại", "Mua sắm", "Nhà cửa", "Giải trí", "Học tập", "Khác"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        String[] frequencies = {"Hàng ngày", "Hàng tuần", "Hàng tháng"};
        spinnerFrequency.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, frequencies));

        cbRecurring.setOnCheckedChangeListener((buttonView, isChecked) ->
                spinnerFrequency.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );
        loadBudget();
        loadExpenses();
        checkAndAddRecurringExpenses();

        btnAdd.setOnClickListener(v -> addExpense());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ExpenseActivity.this, EditExpenseActivity.class);
            intent.putExtra("expenseItem", expenses.get(position));
            intent.putExtra("position", position);
            startActivityForResult(intent, 100);
        });

        updateExpenseDisplay();
    }

    private void addExpense() {
        String name = edtName.getText().toString();
        String qtyStr = edtQuantity.getText().toString();
        String priceStr = edtPrice.getText().toString();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);
            String category = spinnerCategory.getSelectedItem().toString();
            boolean isRec = cbRecurring.isChecked();
            String freq = isRec ? spinnerFrequency.getSelectedItem().toString() : "";

            ExpenseItem item = new ExpenseItem(name, quantity, price, category, isRec, freq);
            expenses.add(item);
            addExpenseString(item);

            adapter.notifyDataSetChanged();
            saveExpenses();
            updateExpenseDisplay();

            checkBudgetWarning();

            edtName.setText("");
            edtQuantity.setText("");
            edtPrice.setText("");
            cbRecurring.setChecked(false);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng hoặc giá không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkBudgetWarning() {
        if (budget <= 0) return;

        double totalSpent = 0;
        for (ExpenseItem e : expenses) totalSpent += e.getTotalPrice();

        double percent = (totalSpent / budget) * 100;

        if (percent >= 100) {
            sendWarningNotification("CẢNH BÁO KHẨN CẤP",
                    "Bạn đã tiêu " + (int) percent + "% ngân sách!");
        } else if (percent >= 80) {
            sendWarningNotification("Cảnh báo",
                    "Bạn đã dùng " + (int) percent + "% ngân sách.");
        }
    }

    private void sendWarningNotification(String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "BUDGET_WARNING", "Cảnh báo ngân sách", NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "BUDGET_WARNING")
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(this).notify(999, builder.build());
        }
    }

    private void addExpenseString(ExpenseItem e) {
        String info = e.getName() + " (" + e.getCategory() + ") - " + e.getTotalPrice();
        if (e.isRecurring()) info += " [Lặp: " + e.getFrequency() + "]";
        expenseStrings.add(info);
    }

    private void updateExpenseDisplay() {
        double totalPrice = 0;
        int totalQty = 0;

        for (ExpenseItem e : expenses) {
            totalQty += e.getQuantity();
            totalPrice += e.getTotalPrice();
        }

        txtTotalQuantity.setText("Tổng số lượng: " + totalQty);
        txtTotalPrice.setText("Tổng chi: " + totalPrice);

        double remaining = budget - totalPrice;
        txtRemaining.setText("Số tiền còn lại: " + remaining);

        if (remaining < 0)
            txtRemaining.setTextColor(getColor(android.R.color.holo_red_dark));
        else
            txtRemaining.setTextColor(getColor(android.R.color.black));
    }

    private void saveExpenses() {
        SharedPreferences prefs = getSharedPreferences("expensePrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        prefs.edit().putString("expenses", gson.toJson(expenses)).apply();
    }

    private void loadExpenses() {
        SharedPreferences prefs = getSharedPreferences("expensePrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("expenses", "[]");

        Type type = new TypeToken<ArrayList<ExpenseItem>>() {
        }.getType();

        expenses = gson.fromJson(json, type);
        if (expenses == null) expenses = new ArrayList<>();

        expenseStrings = new ArrayList<>();
        for (ExpenseItem e : expenses) addExpenseString(e);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseStrings);
        listView.setAdapter(adapter);
    }

    private void loadBudget() {
        SharedPreferences prefsBudget = getSharedPreferences("budgetPrefs", MODE_PRIVATE);
        try {
            budget = Double.parseDouble(prefsBudget.getString("budget", "0"));
        } catch (Exception e) {
            budget = 0;
        }
        txtBudget.setText("Ngân sách: " + budget);
    }

    private void checkAndAddRecurringExpenses() {
        // TODO: Tương tự logic bạn đã viết ở bước trước
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            int pos = data.getIntExtra("position", -1);

            if (data.getBooleanExtra("delete", false)) {
                expenses.remove(pos);
                expenseStrings.remove(pos);

                adapter.notifyDataSetChanged();
                saveExpenses();
                updateExpenseDisplay();
                return;
            }

            ExpenseItem updatedItem = (ExpenseItem) data.getSerializableExtra("updatedItem");

            expenses.set(pos, updatedItem);
            expenseStrings.set(pos, updatedItem.getName() + " (" +
                    updatedItem.getCategory() + ") - " + updatedItem.getTotalPrice());

            adapter.notifyDataSetChanged();
            saveExpenses();
            updateExpenseDisplay();
        }
    }
}
