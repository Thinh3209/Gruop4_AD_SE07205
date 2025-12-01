package com.example.asm2;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OfferActivity extends AppCompatActivity {

    ListView lvOffers;
    ArrayList<OfferItem> offerList;
    OfferAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        lvOffers = findViewById(R.id.lvOffers);
        offerList = new ArrayList<>();

        // --- TẠO DỮ LIỆU GIẢ LẬP (MOCK DATA) ---
        offerList.add(new OfferItem("Trà sữa GongCha", "Giảm 50% cho size L", 60000, 30000));
        offerList.add(new OfferItem("Vé xem phim CGV", "Đồng giá vé 2D cuối tuần", 110000, 49000));
        offerList.add(new OfferItem("Nhà sách Fahasa", "Voucher mua dụng cụ học tập", 100000, 75000));
        offerList.add(new OfferItem("Highlands Coffee", "Mua 1 tặng 1 (Phin sữa đá)", 58000, 29000));
        offerList.add(new OfferItem("GrabBike", "Mã giảm giá đi học sáng sớm", 25000, 10000));
        offerList.add(new OfferItem("Spotify Student", "Gói Premium 1 tháng", 59000, 29500));
        // ---------------------------------------

        adapter = new OfferAdapter(this, offerList);
        lvOffers.setAdapter(adapter);
    }

    // --- CLASS ADAPTER TÙY CHỈNH (Nằm trong file này luôn cho gọn) ---
    class OfferAdapter extends ArrayAdapter<OfferItem> {
        public OfferAdapter(@NonNull Context context, ArrayList<OfferItem> list) {
            super(context, 0, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_offer_item, parent, false);
            }

            OfferItem item = getItem(position);

            TextView txtTitle = convertView.findViewById(R.id.txtOfferTitle);
            TextView txtDesc = convertView.findViewById(R.id.txtOfferDesc);
            TextView txtOldPrice = convertView.findViewById(R.id.txtOldPrice);
            TextView txtNewPrice = convertView.findViewById(R.id.txtNewPrice);
            Button btnGet = convertView.findViewById(R.id.btnGetOffer);

            txtTitle.setText(item.getTitle());
            txtDesc.setText(item.getDescription());

            // Định dạng tiền tệ
            txtOldPrice.setText(String.format("%,.0f đ", item.getOriginalPrice()));
            txtNewPrice.setText(String.format("%,.0f đ", item.getDiscountPrice()));

            // Gạch ngang giá cũ (Hiệu ứng giảm giá)
            txtOldPrice.setPaintFlags(txtOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Sự kiện bấm nút LẤY
            btnGet.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Đã lưu Voucher: " + item.getTitle() + " vào ví!", Toast.LENGTH_SHORT).show();
                btnGet.setText("ĐÃ LẤY");
                btnGet.setEnabled(false); // Không cho bấm lại
                btnGet.setBackgroundColor(0xFF888888); // Đổi màu xám
            });

            return convertView;
        }
    }
}