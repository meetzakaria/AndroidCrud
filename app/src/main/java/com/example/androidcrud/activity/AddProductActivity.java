package com.example.androidcrud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidcrud.R;
import com.example.androidcrud.model.Product;
import com.example.androidcrud.service.ApiService;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddProductActivity extends AppCompatActivity {

    private static final int FILE_PICK_CODE = 2000;

    private EditText ProductName, Description, Price, StockQuantity;
    private Button btnSave;
    private TextView selectedFileText;
    private Spinner categorySpinner;

    private ApiService apiService;
    private Uri selectedFileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        ProductName = findViewById(R.id.ProductName);
        Description = findViewById(R.id.Description);
        Price = findViewById(R.id.Price);
        StockQuantity = findViewById(R.id.StockQuantity);
        btnSave = findViewById(R.id.btnSave);
        selectedFileText = findViewById(R.id.selectedFileText);
        categorySpinner = findViewById(R.id.category);

        // Setup Spinner
        String[] categories = {"Seeds", "Equipment", "Fertilizer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // File chooser
        Button btnChooseFile = findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), FILE_PICK_CODE);
        });

        // Retrofit initialization
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.0.119:8081/") // change IP as needed
                .baseUrl("http://192.168.53.45:8081/") // change IP as needed
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String productName = ProductName.getText().toString().trim();
        String description = Description.getText().toString().trim();
        String price = Price.getText().toString().trim();
        String quantity = StockQuantity.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (productName.isEmpty() || description.isEmpty() || price.isEmpty()
                || quantity.isEmpty() || selectedFileUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare multipart data
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody qtyBody = RequestBody.create(MediaType.parse("text/plain"), quantity);
        RequestBody categoryBody = RequestBody.create(MediaType.parse("text/plain"), category);
        MultipartBody.Part imagePart = prepareFilePart("image", selectedFileUri);

        if (imagePart == null) {
            Toast.makeText(this, "Failed to prepare image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API
        Call<Product> call = apiService.saveProduct(nameBody, descBody, priceBody, qtyBody, categoryBody, imagePart);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    startActivity(new Intent(AddProductActivity.this, ProductListActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddProductActivity.this, "Save failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                Toast.makeText(AddProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), fileBytes);
            return MultipartBody.Part.createFormData(partName, getFileName(fileUri), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void clearForm() {
        ProductName.setText("");
        Description.setText("");
        Price.setText("");
        StockQuantity.setText("");
        selectedFileText.setText("No file selected");
        categorySpinner.setSelection(0);
        selectedFileUri = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            selectedFileText.setText("Selected: " + getFileName(selectedFileUri));
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null && uri != null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
