//package com.example.androidcrud.activity;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.androidcrud.R;
//import com.example.androidcrud.adapter.ProductAdapter;
//import com.example.androidcrud.model.Product;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductListActivity extends AppCompatActivity {
//
//    RecyclerView recyclerView;
//    ProductAdapter productAdapter;
//    List<Product> productList;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_product_list);
//
//        recyclerView = findViewById(R.id.ProductRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        productList = new ArrayList<>();
//        productList.add(new Product(1L, "Product A", "Best product", 200.0, 10, "Electronics", null));
//        productList.add(new Product(2L, "Product B", "Another item", 350.0, 5, "Fashion", null));
//
//        productAdapter = new ProductAdapter(this, productList);
//        recyclerView.setAdapter(productAdapter);
//    }
//}
