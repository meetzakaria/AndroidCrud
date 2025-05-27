package com.example.androidcrud;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidcrud.activity.addProductActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        Button btnProductList = findViewById(R.id.btnProductList);

        btnAddProduct.setOnClickListener(v -> navigateToAddProductPage());
        btnProductList.setOnClickListener(v -> navigateToProductListPage());
    }



    private void navigateToAddProductPage() {
        Intent intent = new Intent(MainActivity.this, addProductActivity.class);
        startActivity(intent);
    }

    private void navigateToProductListPage() {














//        Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
//        startActivity(intent);
    }

}