package com.example.asm2;

import java.io.Serializable;

public class ExpenseItem implements Serializable {
    private String name;
    private int quantity;
    private double price;
    private String category;

    // --- CÁC TRƯỜNG MỚI CHO CHỨC NĂNG LẶP LẠI ---
    private boolean isRecurring; // Có phải là chi phí thường xuyên không?
    private String frequency;    // "Hàng ngày", "Hàng tuần", "Hàng tháng"
    private long lastAddedTime;  // Thời gian lần cuối khoản này được thêm (tính bằng mili giây)

    public ExpenseItem(String name, int quantity, double price, String category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.isRecurring = false; // Mặc định là không lặp
        this.lastAddedTime = System.currentTimeMillis();
    }

    // Constructor đầy đủ
    public ExpenseItem(String name, int quantity, double price, String category, boolean isRecurring, String frequency) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.isRecurring = isRecurring;
        this.frequency = frequency;
        this.lastAddedTime = System.currentTimeMillis();
    }

    // Getters
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getCategory() { return category == null ? "Khác" : category; }
    public double getTotalPrice() { return quantity * price; }

    public boolean isRecurring() { return isRecurring; }
    public String getFrequency() { return frequency; }
    public long getLastAddedTime() { return lastAddedTime; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setLastAddedTime(long time) { this.lastAddedTime = time; }
}