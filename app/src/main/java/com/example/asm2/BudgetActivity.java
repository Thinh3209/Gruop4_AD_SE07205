package com.example.asm2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BudgetActivity extends AppCompatActivity {

    EditText edtBudget;
    Button btnSaveBudget;
    TextView txtTotalExpense, txtRemaining;

    double budget = 0;
    ArrayList<ExpenseItem> expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        edtBudget = findViewById(R.id.edtBudget);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        txtTotalExpense = findViewById(R.id.txtTotalExpense);
        txtRemaining = findViewById(R.id.txtRemaining);

        // 1. Lấy ngân sách đã lưu
        SharedPreferences prefsBudget = getSharedPreferences("budgetPrefs", MODE_PRIVATE);
        String budgetStr = prefsBudget.getString("budget", "0");
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            budget = 0;
        }
        edtBudget.setText(budgetStr.equals("0") ? "" : budgetStr);

        // 2. Lấy danh sách chi phí từ SharedPreferences (Thay vì Intent)
        loadExpensesFromPrefs();

        // 3. Hiển thị thông tin
        updateExpenseDisplay();

        // 4. Sự kiện lưu ngân sách mới
        btnSaveBudget.setOnClickListener(v -> {
            String input = edtBudget.getText().toString();
            if (input.isEmpty()) {
                Toast.makeText(this, "Nhập ngân sách!", Toast.LENGTH_SHORT).show();
                return;
            }
            budget = Double.parseDouble(input);

            // Lưu vào SharedPreferences
            SharedPreferences.Editor editor = prefsBudget.edit();
            editor.putString("budget", String.valueOf(budget));
            editor.apply();

            Toast.makeText(this, "Đã lưu ngân sách: " + budget, Toast.LENGTH_SHORT).show();
            updateExpenseDisplay();
        });
    }

    private void loadExpensesFromPrefs() {
        SharedPreferences prefsExp = getSharedPreferences("expensePrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefsExp.getString("expenses", "[]");
        Type type = new TypeToken<ArrayList<ExpenseItem>>(){}.getType();
        expenses = gson.fromJson(json, type);

        if (expenses == null) expenses = new ArrayList<>();
    }

    private void updateExpenseDisplay() {
        double totalExpense = 0;
        for (ExpenseItem e : expenses) {
            totalExpense += e.getTotalPrice();
        }
        txtTotalExpense.setText("Tổng chi hiện tại: " + totalExpense);

        double remaining = budget - totalExpense;
        txtRemaining.setText("Số tiền còn lại: " + remaining);

        // Đổi màu cảnh báo nếu tiêu quá lố
        if (remaining < 0) {
            txtRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            txtRemaining.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}