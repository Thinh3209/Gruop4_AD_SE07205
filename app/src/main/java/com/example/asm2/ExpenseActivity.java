package com.example.asm2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExpenseActivity extends AppCompatActivity {

    EditText edtName, edtQuantity, edtPrice;
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

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtPrice = findViewById(R.id.edtPrice);
        btnAdd = findViewById(R.id.btnAdd);
        listView = findViewById(R.id.listView);
        txtTotalQuantity = findViewById(R.id.txtTotalQuantity);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtBudget = findViewById(R.id.txtBudget);
        txtRemaining = findViewById(R.id.txtRemaining);

        // Load ngân sách từ SharedPreferences
        SharedPreferences prefsBudget = getSharedPreferences("budgetPrefs", MODE_PRIVATE);
        String budgetStr = prefsBudget.getString("budget", "0");
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            budget = 0;
        }
        txtBudget.setText("Ngân sách: " + budget);

        // Load chi phí đã lưu
        loadExpenses();

        btnAdd.setOnClickListener(v -> addExpense());

        // Chỉnh sửa/xóa khi click item trong ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ExpenseItem selectedItem = expenses.get(position);
            Intent intent = new Intent(ExpenseActivity.this, EditExpenseActivity.class);
            intent.putExtra("expenseItem", selectedItem);
            intent.putExtra("position", position);
            startActivityForResult(intent, 1);
        });

        updateExpenseDisplay();
    }

    private void addExpense() {
        String name = edtName.getText().toString();
        String qtyStr = edtQuantity.getText().toString();
        String priceStr = edtPrice.getText().toString();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        double price;
        try {
            quantity = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        ExpenseItem item = new ExpenseItem(name, quantity, price);
        expenses.add(item);
        expenseStrings.add(name + " - SL: " + quantity + " - Giá: " + price);

        adapter.notifyDataSetChanged();
        saveExpenses();
        updateExpenseDisplay();

        edtName.setText("");
        edtQuantity.setText("");
        edtPrice.setText("");
    }

    private void updateExpenseDisplay() {
        int totalQty = 0;
        double totalPrice = 0;
        for (ExpenseItem e : expenses) {
            totalQty += e.getQuantity();
            totalPrice += e.getTotalPrice();
        }

        txtTotalQuantity.setText("Tổng số lượng: " + totalQty);
        txtTotalPrice.setText("Tổng chi: " + totalPrice);

        double remaining = budget - totalPrice;
        txtRemaining.setText("Số tiền còn lại: " + remaining);

        if (remaining < 0) {
            txtRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            txtRemaining.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void saveExpenses() {
        SharedPreferences prefs = getSharedPreferences("expensePrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(expenses);
        prefs.edit().putString("expenses", json).apply();
    }

    private void loadExpenses() {
        SharedPreferences prefs = getSharedPreferences("expensePrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("expenses", "[]");
        Type type = new TypeToken<ArrayList<ExpenseItem>>(){}.getType();
        expenses = gson.fromJson(json, type);

        if (expenses == null) expenses = new ArrayList<>();
        expenseStrings = new ArrayList<>();
        for (ExpenseItem e : expenses) {
            expenseStrings.add(e.getName() + " - SL: " + e.getQuantity() + " - Giá: " + e.getPrice());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseStrings);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            if (position == -1) return;

            if (data.getBooleanExtra("delete", false)) {
                expenses.remove(position);
                expenseStrings.remove(position);
            } else {
                ExpenseItem updatedItem = (ExpenseItem) data.getSerializableExtra("updatedItem");
                expenses.set(position, updatedItem);
                expenseStrings.set(position, updatedItem.getName() + " - SL: " + updatedItem.getQuantity() + " - Giá: " + updatedItem.getPrice());
            }
            adapter.notifyDataSetChanged();
            saveExpenses();
            updateExpenseDisplay();
        }
    }
}
