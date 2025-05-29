package com.example.androidcrud.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidcrud.R;
import com.example.androidcrud.adapter.ProductAdapter;
import com.example.androidcrud.model.Product;
import com.example.androidcrud.service.ApiService;
import com.example.androidcrud.util.ApiClient;
import com.example.androidcrud.util.ProductDiffCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "Product List Activity";
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> ProductList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        productAdapter = new ProductAdapter(this, ProductList);
        recyclerView = findViewById(R.id.ProductRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        fetchProduct();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchProduct() {
        ApiService apiService = ApiClient.getApiService();
        Call<List<Product>> call = apiService.getAllProduct();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> newProducts = response.body();

                    // Efficient RecyclerView update using DiffUtil
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                            new ProductDiffCallback(ProductList, newProducts)
                    );

                    ProductList.clear();
                    ProductList.addAll(newProducts);
                    diffResult.dispatchUpdatesTo(productAdapter); // ✅ use the correct adapter

                    Log.d(TAG, "Product list updated. Total: " + newProducts.size());
                } else {
                    showToast("Server error: " + response.code());
                    Log.e(TAG, "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                showToast("Failed to fetch Products: " + t.getMessage());
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
