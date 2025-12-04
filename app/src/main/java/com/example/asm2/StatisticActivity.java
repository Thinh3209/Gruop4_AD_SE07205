package com.example.asm2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StatisticActivity extends AppCompatActivity {

    ProgressBar progressBarBudget;
    TextView txtBudgetStatus, txtCategoryStats, txtReportTitle;
    Button btnFilterDay, btnFilterWeek, btnFilterMonth;

    // Khai báo thêm ListView và Adapter
    ListView lvDetailList;
    ArrayList<String> detailStrings;
    ArrayAdapter<String> detailAdapter;

    ArrayList<ExpenseItem> allExpenses;
    double budget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // Ánh xạ View cũ
        progressBarBudget = findViewById(R.id.progressBarBudget);
        txtBudgetStatus = findViewById(R.id.txtBudgetStatus);
        txtCategoryStats = findViewById(R.id.txtCategoryStats);
        txtReportTitle = findViewById(R.id.txtReportTitle);
        btnFilterDay = findViewById(R.id.btnFilterDay);
        btnFilterWeek = findViewById(R.id.btnFilterWeek);
        btnFilterMonth = findViewById(R.id.btnFilterMonth);

        // Ánh xạ ListView MỚI
        lvDetailList = findViewById(R.id.lvDetailList);
        detailStrings = new ArrayList<>();
        detailAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, detailStrings);
        lvDetailList.setAdapter(detailAdapter);

        loadData();

        // Mặc định hiển thị toàn bộ
        filterByTime("all"); // Sửa lại logic mặc định

        // Sự kiện các nút lọc
        btnFilterDay.setOnClickListener(v -> filterByTime("day"));
        btnFilterWeek.setOnClickListener(v -> filterByTime("week"));
        btnFilterMonth.setOnClickListener(v -> filterByTime("month"));
    }

    private void loadData() {
        SharedPreferences prefsBudget = getSharedPreferences("budgetPrefs", MODE_PRIVATE);
        try {
            budget = Double.parseDouble(prefsBudget.getString("budget", "0"));
        } catch (Exception e) { budget = 0; }

        SharedPreferences prefsExp = getSharedPreferences("expensePrefs", MODE_PRIVATE);
        String json = prefsExp.getString("expenses", "[]");
        Type type = new TypeToken<ArrayList<ExpenseItem>>(){}.getType();
        allExpenses = new Gson().fromJson(json, type);
        if (allExpenses == null) allExpenses = new ArrayList<>();
    }

    private void filterByTime(String type) {
        ArrayList<ExpenseItem> filteredList = new ArrayList<>();
        Calendar current = Calendar.getInstance();
        String title = "";

        if (type.equals("all")) {
            filteredList.addAll(allExpenses);
            title = "Toàn bộ thời gian";
        } else {
            for (ExpenseItem item : allExpenses) {
                Calendar itemTime = Calendar.getInstance();
                itemTime.setTimeInMillis(item.getLastAddedTime());

                boolean match = false;
                if (type.equals("day")) {
                    match = (itemTime.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) &&
                            (itemTime.get(Calendar.YEAR) == current.get(Calendar.YEAR));
                    title = "Hôm nay";
                } else if (type.equals("week")) {
                    match = (itemTime.get(Calendar.WEEK_OF_YEAR) == current.get(Calendar.WEEK_OF_YEAR)) &&
                            (itemTime.get(Calendar.YEAR) == current.get(Calendar.YEAR));
                    title = "Tuần này";
                } else if (type.equals("month")) {
                    match = (itemTime.get(Calendar.MONTH) == current.get(Calendar.MONTH)) &&
                            (itemTime.get(Calendar.YEAR) == current.get(Calendar.YEAR));
                    title = "Tháng này";
                }

                if (match) {
                    filteredList.add(item);
                }
            }
        }

        calculateStats(filteredList, title);
    }

    private void calculateStats(ArrayList<ExpenseItem> dataList, String title) {
        txtReportTitle.setText("Thống kê: " + title);

        double totalSpent = 0;
        HashMap<String, Double> categoryMap = new HashMap<>();

        // 1. Xử lý danh sách chi tiết (List View)
        detailStrings.clear(); // Xóa dữ liệu cũ trên list
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

        for (ExpenseItem item : dataList) {
            double cost = item.getTotalPrice();
            totalSpent += cost;

            // Cộng dồn cho báo cáo nhóm
            String cat = item.getCategory();
            if (categoryMap.containsKey(cat)) {
                categoryMap.put(cat, categoryMap.get(cat) + cost);
            } else {
                categoryMap.put(cat, cost);
            }

            // Thêm vào danh sách chi tiết (MỚI)
            String timeStr = sdf.format(new Date(item.getLastAddedTime()));
            String detail = item.getName() + " (" + item.getCategory() + ")\n" +
                    "SL: " + item.getQuantity() + " - Tiền: " + String.format("%,.0f", cost) + "\n" +
                    "Ngày: " + timeStr;
            detailStrings.add(detail);
        }
        detailAdapter.notifyDataSetChanged(); // Cập nhật ListView

        // 2. Cập nhật ProgressBar
        int percentage = 0;
        if (budget > 0) {
            percentage = (int) ((totalSpent / budget) * 100);
        }
        progressBarBudget.setProgress(percentage);

        if (percentage > 100) progressBarBudget.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        else if (percentage > 80) progressBarBudget.getProgressDrawable().setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
        else progressBarBudget.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);

        txtBudgetStatus.setText("Tổng chi: " + String.format("%,.0f", totalSpent) + " / Ngân sách: " + String.format("%,.0f", budget));

        // 3. Hiển thị báo cáo tổng hợp nhóm (Text View)
        StringBuilder statsText = new StringBuilder();
        if (categoryMap.isEmpty()) {
            statsText.append("Không có dữ liệu.");
        } else {
            statsText.append("Tỷ lệ chi tiêu:\n");
            for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
                double percentCat = (entry.getValue() / totalSpent) * 100;
                statsText.append("• ").append(entry.getKey())
                        .append(": ").append(String.format("%,.0f", entry.getValue()))
                        .append(" (").append(String.format("%.1f", percentCat)).append("%)  ");
            }
        }
        txtCategoryStats.setText(statsText.toString());
    }
}