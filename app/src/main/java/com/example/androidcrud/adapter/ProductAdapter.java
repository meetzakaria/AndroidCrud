package com.example.androidcrud.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidcrud.R;
import com.example.androidcrud.activity.AddProductActivity;
import com.example.androidcrud.model.Product;
import com.example.androidcrud.service.ApiService;
import com.example.androidcrud.util.ApiClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private ApiService apiService;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.apiService = ApiClient.getApiService();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productCategory, productDescription, productQuantity;
        ImageButton updateButton, deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
            productDescription = itemView.findViewById(R.id.productDescription);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("৳ " + product.getPrice());
        holder.productCategory.setText("Category: " + product.getCategory());
        holder.productDescription.setText(product.getDescription());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());

        // ✅ Image loading from Base64
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(product.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.productImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.productImage.setImageResource(R.drawable.ic_placeholder); // fallback
            }
        } else {
            holder.productImage.setImageResource(R.drawable.ic_placeholder);
        }

        holder.updateButton.setOnClickListener(v -> {
            Log.d("Update", "Update clicked for " + product.getName());
            Toast.makeText(context, "Update button clicked ", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, AddProductActivity.class);
//            intent.putExtra("employee", new Gson().toJson(product));
//            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            Log.d("Delete", "Delete clicked for " + product.getName());
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete " + product.getName() + "?")
                    .setPositiveButton("Yes",
                            (dialog, which) -> apiService.deleteProduct(product.getId())
                                    .enqueue(new Callback<>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                int adapterPosition = holder.getAdapterPosition();
                                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                                    productList.remove(adapterPosition);
                                                    notifyItemRemoved(adapterPosition);
                                                    notifyItemRangeChanged(adapterPosition, productList.size());
                                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

//    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
//        TextView nameText, emailText, designationText;
//        ImageButton updateButton, deleteButton;
//
//        public EmployeeViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameText = itemView.findViewById(R.id.nameText);
//            emailText = itemView.findViewById(R.id.emailText);
//            designationText = itemView.findViewById(R.id.designationText);
//            updateButton = itemView.findViewById(R.id.updateButton);
//            deleteButton = itemView.findViewById(R.id.deleteButton);
//        }
//    }
}
