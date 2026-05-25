package com.example.banhang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banhang.R;
import com.example.banhang.adapter.CartAdapter;
import com.example.banhang.model.CartItem;
import com.example.banhang.sqlite.CartDAO;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {


    private ImageButton btnBack;
    private TextView tvHeaderTitle;
    private RecyclerView rvCartItems;
    private EditText etPromoCode;
    private TextView btnApplyPromo;
    private TextView tvSubtotal;
    private TextView tvTotal;
    private MaterialButton btnCheckout;

    // Đối tượng xử lý dữ liệu
    private CartDAO cartDAO;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList;
    private int currentCartId;

    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        SharedPreferences sessionPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        currentUserId = sessionPref.getInt("current_user_id", -1);


        if (currentUserId == -1) {
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }


        initViews();
        cartDAO = new CartDAO(this);
        setupRecyclerView();
        setupActivityEvents();
        loadCartData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        rvCartItems = findViewById(R.id.rvCartItems);
        etPromoCode = findViewById(R.id.etPromoCode);
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnOrderHistory).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartList == null || cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        cartList = new ArrayList<>();
        // Khởi tạo adapter và truyền 'this' làm listener để bắt sự kiện tăng/giảm/xóa
        cartAdapter = new CartAdapter(this, cartList, this);

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);
        // Ngăn RecyclerView chiếm quyền cuộn của NestedScrollView cha bên ngoài
        rvCartItems.setNestedScrollingEnabled(false);
    }

    private void setupActivityEvents() {
        // Nút quay lại trang trước
        btnBack.setOnClickListener(v -> finish());

        // Nút áp dụng mã giảm giá
        btnApplyPromo.setOnClickListener(v -> {
            String promo = etPromoCode.getText().toString().trim();
            if (promo.isEmpty()) {
                Toast.makeText(CartActivity.this, "Please enter a promo code", Toast.LENGTH_SHORT).show();
            } else {
                // Xử lý logic mã giảm giá tại đây nếu cần
                Toast.makeText(CartActivity.this, "Promo code '" + promo + "' applied successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút tiến hành thanh toán an toàn
        btnCheckout.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadCartData() {

        currentCartId = cartDAO.getOrCreateCartId(currentUserId);


        List<CartItem> itemsFromDb = cartDAO.getCartItems(currentCartId);

        // Cập nhật vào danh sách hiển thị của Adapter
        cartList.clear();
        cartList.addAll(itemsFromDb);
        cartAdapter.notifyDataSetChanged();

        // Tính toán và hiển thị tổng số tiền của giỏ hàng
        updateCartTotalSummary();
    }

    /**
     * Hàm tính toán tổng tiền tự động dựa trên số lượng và giá sản phẩm
     */
    private void updateCartTotalSummary() {
        double subtotal = 0;
        for (CartItem item : cartList) {
            if (item.getProduct() != null) {
                subtotal += (item.getProduct().getPrice() * item.getQuantity());
            }
        }

        // Định dạng hiển thị tiền tệ dạng Đô la giống như file CSV  ($12.34)
        String formattedPrice = String.format(Locale.US, "$%.2f", subtotal);

        // Nếu muốn hiển thị tiền Việt (VND) thì mở comment dòng dưới và khóa dòng trên lại:
        // String formattedPrice = String.format(Locale.GERMANY, "%,.0f ₫", subtotal);

        tvSubtotal.setText(formattedPrice);
        tvTotal.setText(formattedPrice);
    }

    // =========================================================================
    // CÁC HÀM OVERRIDE XỬ LÝ SỰ KIỆN TỪ CARTADAPTER (NHẬN TỪ NÚT TĂNG/GIẢM/XÓA)
    // =========================================================================

    @Override
    public void onIncreaseQuantity(CartItem item, int position) {
        int newQty = item.getQuantity() + 1;
        // 1. Cập nhật DB
        cartDAO.updateQuantity(item.getCartItemId(), newQty);
        // 2. Cập nhật UI
        item.setQuantity(newQty);
        cartAdapter.notifyItemChanged(position);
        updateCartTotalSummary(); // Hàm tính lại tổng tiền
    }

    @Override
    public void onDecreaseQuantity(CartItem item, int position) {
        int newQty = item.getQuantity() - 1;
        if (newQty > 0) {
            cartDAO.updateQuantity(item.getCartItemId(), newQty);
            item.setQuantity(newQty);
            cartAdapter.notifyItemChanged(position);
        } else {
            // Tự động gọi xóa khi giảm xuống 0
            onRemoveItem(item, position);
            return;
        }
        updateCartTotalSummary();
    }

    @Override
    public void onRemoveItem(CartItem item, int position) {
        cartDAO.deleteCartItem(item.getCartItemId());
        cartList.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartList.size());
        updateCartTotalSummary();
    }
}