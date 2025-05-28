package com.example.androidcrud.service;

import com.example.androidcrud.model.Product;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/api/products")
    Call<Product> saveProduct(@Body Product product);

    @GET("Product")
    Call<List<Product>> getAllProduct();

    @GET("Product/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @PUT("Product/{id}")
    Call<Product> updateProduct(@Path("id") int id, @Body Product Product);

    @DELETE("Product/{id}")
    Call<Void> deleteProduct(@Path("id") int id);

}