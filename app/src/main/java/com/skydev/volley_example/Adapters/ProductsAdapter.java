package com.skydev.volley_example.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skydev.volley_example.Listeners.ProductsListener;
import com.skydev.volley_example.Models.Product;
import com.skydev.volley_example.databinding.ProductListItemBinding;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.TodosViewHolder>{
    private List<Product> productList;
    private final ProductsListener productsListener;

    public ProductsAdapter(List<Product> productList, ProductsListener productsListener) {
        this.productList = productList;
        this.productsListener = productsListener;
    }

    @NonNull
    @Override
    public ProductsAdapter.TodosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ProductListItemBinding productListItemBinding = ProductListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ProductsAdapter.TodosViewHolder(productListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.TodosViewHolder holder, int position) {

        holder.setTodoData(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class TodosViewHolder extends RecyclerView.ViewHolder{

        private ProductListItemBinding binding;

        public TodosViewHolder(ProductListItemBinding productListItemBinding) {
            super(productListItemBinding.getRoot());
            binding = productListItemBinding;
        }

        void setTodoData(Product product){
            binding.productName.setText(product.name);
            binding.productCheckBox.setChecked(product.enabled);
            binding.productCheckBox.setOnClickListener(v -> productsListener.onChangeStatus(product, binding));
            binding.getRoot().setOnClickListener(v -> productsListener.onViewDetails(product.id_product));
        }
    }
}
