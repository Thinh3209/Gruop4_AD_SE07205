package com.example.asm2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText edtRegUser, edtRegPass; // Đặt tên biến khớp với logic
    Button btnRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);


        edtRegUser = findViewById(R.id.edtRegUser);
        edtRegPass = findViewById(R.id.edtRegPass);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String user = edtRegUser.getText().toString().trim();
            String pass = edtRegPass.getText().toString().trim();

            if(user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Gọi hàm insertUser từ DBHelper
                boolean checkInsert = dbHelper.insertUser(user, pass);
                if(checkInsert) {
                    Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show();
                    // Chuyển ngay về màn hình đăng nhập
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}