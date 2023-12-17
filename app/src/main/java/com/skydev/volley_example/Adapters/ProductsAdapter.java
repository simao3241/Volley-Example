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

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>{
    private List<Product> productList;
    private final ProductsListener productsListener;

    public ProductsAdapter(List<Product> productList, ProductsListener productsListener) {
        this.productList = productList;
        this.productsListener = productsListener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ProductListItemBinding productListItemBinding = ProductListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ProductsAdapter.ProductsViewHolder(productListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ProductsViewHolder holder, int position) {

        holder.setTodoData(productList.get(position));
    }

    public void onDelete(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ProductsViewHolder) holder).deleteEvent(productList.get(position));
    }

    public void onEdit(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ProductsViewHolder) holder).editEvent(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder{

        private ProductListItemBinding binding;

        public ProductsViewHolder(ProductListItemBinding productListItemBinding) {
            super(productListItemBinding.getRoot());
            binding = productListItemBinding;
        }

        void setTodoData(Product product) {
            binding.productName.setText(product.name);
            binding.productCheckBox.setChecked(product.enabled);
            binding.productCheckBox.setOnClickListener(v -> product.enabled = productsListener.onChangeStatus(product, binding));
            binding.getRoot().setOnClickListener(v -> productsListener.onViewDetails(product.id_product, binding));
        }

        public void deleteEvent(Product product) {
            productsListener.onProductDelete(product);
        }
        public void editEvent(Product product) {
            productsListener.onProductEdit(product);
        }
    }
}
