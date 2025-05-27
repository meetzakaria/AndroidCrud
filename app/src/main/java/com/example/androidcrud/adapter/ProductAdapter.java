package com.example.androidcrud.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidcrud.R;
import com.example.androidcrud.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, productCategory, productDescription, productQuantity;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCategory = itemView.findViewById(R.id.productCategory);
            productDescription = itemView.findViewById(R.id.productDescription);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("৳ " + product.getPrice());
        holder.productCategory.setText("Category: " + product.getCategory());
        holder.productDescription.setText(product.getDescription());
        holder.productQuantity.setText("Quantity: " + product.getQuantity());

        if (product.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
            holder.productImage.setImageBitmap(bitmap);
        } else {
            holder.productImage.setImageResource(R.drawable.ic_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
