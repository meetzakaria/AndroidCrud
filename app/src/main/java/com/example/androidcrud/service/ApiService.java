package com.example.androidcrud.service;

import com.example.androidcrud.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

//    @POST("api/products/add")
//    Call<Product> saveProduct(@Body Product product);

    @Multipart
    @POST("api/products/add")
    Call<Product> saveProduct(
            @Part("name") RequestBody name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("quantity") RequestBody quantity,
            @Part("category") RequestBody category,
            @Part MultipartBody.Part image
    );

    @GET("api/products")
    Call<List<Product>> getAllProduct();

    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @PUT("api/products/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body Product Product);

    @DELETE("api/products/{id}")
    Call<Void> deleteProduct(@Path("id") long id);

    Call<Product> saveProduct(Product product);
}