package com.skydev.volley_example.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.skydev.volley_example.Adapters.ProductsAdapter;
import com.skydev.volley_example.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeItem extends androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback{

    ProductsAdapter productsAdapter;
    private final Context context;

    public SwipeItem(ProductsAdapter productsAdapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.productsAdapter = productsAdapter;
        this.context = context;
    }

    @Override
    public boolean onMove(androidx.recyclerview.widget.RecyclerView recyclerView, androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            this.productsAdapter.onEdit(viewHolder, viewHolder.getAdapterPosition());
        }
        else if (direction == ItemTouchHelper.LEFT)
            this.productsAdapter.onDelete(viewHolder, viewHolder.getAdapterPosition());
    }
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(this.context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightActionIcon(R.drawable.ic_edit)
                .addSwipeRightLabel(context.getString(R.string.editButton))
                .addSwipeRightBackgroundColor(context.getColor(R.color.green))
                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                .addSwipeLeftLabel(context.getString(R.string.deleteButton))
                .addSwipeLeftBackgroundColor(context.getColor(R.color.red))
                .setSwipeLeftLabelColor(context.getColor(R.color.gray_100))
                .setSwipeRightLabelColor(context.getColor(R.color.gray_100))
                .setActionIconTint(context.getColor(R.color.gray_100))
//                .addSwipeLeftBackgroundColor(context.getColor(R.color.error_500)
//                .addActionIcon(R.drawable.ic_delete)
//                .addSwipeRightBackgroundColor(context.getColor(R.color.success_500)
//                .addSwipeRightActionIcon(R.drawable.ic_edit)
                .create()
                .decorate();


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
