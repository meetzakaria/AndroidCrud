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

import java.io.File;
import java.io.FileOutputStream;
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
    private boolean isEditMode = false;
    private int employeeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ProductName = findViewById(R.id.ProductName);
        Description = findViewById(R.id.Description);
        Price = findViewById(R.id.Price);
        StockQuantity = findViewById(R.id.StockQuantity);
        btnSave = findViewById(R.id.btnSave);
        selectedFileText = findViewById(R.id.selectedFileText);
        categorySpinner = findViewById(R.id.category);

        // Spinner
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

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.0.100:8081/")
//                .baseUrl("http://192.168.0.119:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String productName = ProductName.getText().toString().trim();
        String description = Description.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String price = Price.getText().toString().trim();
        String quantity = StockQuantity.getText().toString().trim();

        if (productName.isEmpty() || description.isEmpty() || price.isEmpty() || quantity.isEmpty() || selectedFileUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody quantityBody = RequestBody.create(MediaType.parse("text/plain"), quantity);
        RequestBody categoryBody = RequestBody.create(MediaType.parse("text/plain"), selectedCategory);

        MultipartBody.Part imagePart = prepareFilePart("image", selectedFileUri);

        Call<Product> call = apiService.saveProduct(
                nameBody,
                descriptionBody,
                priceBody,
                quantityBody,
                categoryBody,
                imagePart
        );
        Product product = null;
        if (isEditMode) {
            product = new Product();
            call = apiService.updateProduct(employeeId, product);
        } else {
            call = apiService.saveProduct(product);
        }

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Product saved successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    startActivity(new Intent(AddProductActivity.this, ProductListActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed: " + response.message(), Toast.LENGTH_SHORT).show();
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


    private File createTempFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        String fileName = getFileName(uri);
        File tempFile = File.createTempFile("upload_", fileName, getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        return tempFile;
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
            String fileName = getFileName(selectedFileUri);
            selectedFileText.setText("Selected: " + fileName);
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
