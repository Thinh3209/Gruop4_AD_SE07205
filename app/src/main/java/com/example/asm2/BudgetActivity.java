package com.example.asm2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BudgetActivity extends AppCompatActivity {

    EditText edtBudget;
    Button btnSaveBudget;
    TextView txtTotalExpense, txtRemaining;

    double budget = 0;
    ArrayList<ExpenseItem> expenses; // Nhận từ Intent hoặc lưu tạm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        edtBudget = findViewById(R.id.edtBudget);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        txtTotalExpense = findViewById(R.id.txtTotalExpense);
        txtRemaining = findViewById(R.id.txtRemaining);

        // Lấy ngân sách đã lưu
        SharedPreferences prefs = getSharedPreferences("budgetPrefs", MODE_PRIVATE);
        budget = Double.parseDouble(prefs.getString("budget", "0"));
        edtBudget.setText(String.valueOf(budget));

        // Lấy danh sách chi phí từ Intent
        expenses = (ArrayList<ExpenseItem>) getIntent().getSerializableExtra("expenses");
        if (expenses == null) expenses = new ArrayList<>();

        updateExpenseDisplay();

        btnSaveBudget.setOnClickListener(v -> {
            String budgetStr = edtBudget.getText().toString();
            if (budgetStr.isEmpty()) {
                Toast.makeText(this, "Nhập ngân sách!", Toast.LENGTH_SHORT).show();
                return;
            }
            budget = Double.parseDouble(budgetStr);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("budget", String.valueOf(budget));
            editor.apply();
            Toast.makeText(this, "Đã lưu ngân sách: " + budget, Toast.LENGTH_SHORT).show();
            updateExpenseDisplay();
        });
    }

    private void updateExpenseDisplay() {
        double totalExpense = 0;
        for (ExpenseItem e : expenses) {
            totalExpense += e.getTotalPrice();
        }
        txtTotalExpense.setText("Tổng chi hiện tại: " + totalExpense);
        double remaining = budget - totalExpense;
        txtRemaining.setText("Số tiền còn lại: " + remaining);
    }
}
