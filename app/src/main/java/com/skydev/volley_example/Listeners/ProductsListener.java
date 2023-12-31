package com.skydev.volley_example.Listeners;

import android.view.View;

import com.skydev.volley_example.Models.Product;
import com.skydev.volley_example.databinding.ProductListItemBinding;

public interface ProductsListener {
    boolean onChangeStatus(Product product, ProductListItemBinding view);
    void onViewDetails(int id_product, ProductListItemBinding view);
    void onProductDelete(Product product);
    void onProductEdit(Product product);
}
