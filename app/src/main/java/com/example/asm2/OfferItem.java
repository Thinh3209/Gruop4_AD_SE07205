package com.example.asm2;

public class OfferItem {
    private String title;
    private String description;
    private double originalPrice;
    private double discountPrice;
    private int imageResId; // Để hiển thị icon (nếu muốn)

    public OfferItem(String title, String description, double originalPrice, double discountPrice) {
        this.title = title;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountPrice() { return discountPrice; }
}