package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnExpense, btnBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ nút button từ activities đến layout
        btnExpense = findViewById(R.id.btnExpense);
        btnBudget = findViewById(R.id.btnBudget);

        // Mở màn hình theo dõi chi phí
        btnExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExpenseActivity.class);
            startActivity(intent);
        });

        // Mở màn hình thiết lập ngân sách
        btnBudget.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);

            // Nếu muốn truyền danh sách chi phí hiện tại
            // intent.putExtra("expenses", expenses);
            // Lưu ý: expenses cần được lấy từ ExpenseActivity hoặc DB

            startActivity(intent);
        });
    }
}
