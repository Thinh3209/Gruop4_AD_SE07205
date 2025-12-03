package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditExpenseActivity extends AppCompatActivity {

    EditText edtName, edtQuantity, edtPrice;
    Button btnSave, btnDelete;

    ExpenseItem expenseItem;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        edtName = findViewById(R.id.edtName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtPrice = findViewById(R.id.edtPrice);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // Nhận dữ liệu từ Intent
        expenseItem = (ExpenseItem) getIntent().getSerializableExtra("expenseItem");
        position = getIntent().getIntExtra("position", -1);

        if (expenseItem != null) {
            edtName.setText(expenseItem.getName());
            edtQuantity.setText(String.valueOf(expenseItem.getQuantity()));
            edtPrice.setText(String.valueOf(expenseItem.getPrice()));
        }

        btnSave.setOnClickListener(v -> saveExpense());
        btnDelete.setOnClickListener(v -> deleteExpense());
    }

    private void saveExpense() {
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

        // Cập nhật ExpenseItem
        expenseItem.setName(name);
        expenseItem.setQuantity(quantity);
        expenseItem.setPrice(price);

        // Trả dữ liệu về ExpenseActivity
        Intent intent = new Intent();
        intent.putExtra("updatedItem", expenseItem);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deleteExpense() {
        // Trả thông tin xóa về ExpenseActivity
        Intent intent = new Intent();
        intent.putExtra("delete", true);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
