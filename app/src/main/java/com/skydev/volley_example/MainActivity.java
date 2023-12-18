package com.skydev.volley_example;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skydev.volley_example.Adapters.ProductsAdapter;
import com.skydev.volley_example.Listeners.ProductsListener;
import com.skydev.volley_example.Models.Product;
import com.skydev.volley_example.Singletons.BaseSingleton;
import com.skydev.volley_example.Util.Constants;
import com.skydev.volley_example.Util.SwipeItem;
import com.skydev.volley_example.databinding.ActivityMainBinding;
import com.skydev.volley_example.databinding.DetailsModalBinding;
import com.skydev.volley_example.databinding.ProductListItemBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductsListener {
    //Declare variables
    private ActivityMainBinding binding;

    ProductsAdapter productsAdapter;
    SwipeItem swipeItem;
    ItemTouchHelper itemTouchHelper;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initialize variables
        init();

        //Set Listeners
        setListeners();
    }

    //Initialize variables
    private void init() {
        getAll();
    }

    //Set the listeners
    private void setListeners() {
        binding.fab.setOnClickListener(v -> showPostModal());
    }

    //Get Queue
    private void getQueue() {
        queue = BaseSingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
    }

    //Get all products
    private void getAll() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL,
                response -> {
//                    makeToast("API Connected");
                    String productsJSON = null;
                    try {
                        JSONObject jsonObject = new JSONObject( response );
                        productsJSON = jsonObject.getJSONObject("data").getJSONArray("products").toString();
//                        Log.d("JSON", productsJSON);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    List<Product> products = (List<Product>) convertData(productsJSON, true);
                    if (products != null) {
                        if (products.size() > 0) {
                            loading(false, false);
                            setRecycler(products);
                        } else {
                            loading(false, true, "No data found");
                        }
                    } else {
                        loading(false, true, "No data found");
                    }
                }, error -> {
                    try {
                        String responseMessage = decodeError(error);
                        if (error.networkResponse.statusCode == 404) {
                            Log.e("APIError", "Error: ");
                            loading(false,
                                    true,
                                    responseMessage
                            );
                        } else {
                            loading(false,
                                    true,
                                    "Ocorreu um erro inesperado, tente novamente mais tarde"
                            );
                        }
                    } catch ( JSONException e ) {
                        //Handle a malformed json response
                        loading(false,
                                true,
                                "Error json ex: " + e.getMessage()
                        );
//                        Log.e("APIError", "Error json ex: " + e.getMessage());
                    } catch (UnsupportedEncodingException exceptionError){
                        loading(false,
                                true,
                                "Error ex: " + exceptionError.getMessage()
                        );
//                        Log.e("APIError", "Error ex: " + exceptionError.getMessage());

                    }
                });

        //Set the tag
        stringRequest.setTag(Constants.TAG);

        // Add the request to the RequestQueue.
        BaseSingleton.getInstance(this).addToRequestQueue(stringRequest);
        //Set loading
        loading(true);
    }

    //Change Status
    @Override
    public boolean onChangeStatus(Product product, ProductListItemBinding view) {
        //Update the product
        view.productCheckBox.setVisibility(View.GONE);
        view.productProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest objectRequest =
                new JsonObjectRequest(Request.Method.POST,
                    Constants.BASE_URL + "/" + product.id_product + "/"
                            + (!product.enabled ? "enable" : "disable"),
                    null,
                    response -> {
                        view.productCheckBox.setVisibility(View.VISIBLE);
                        view.productProgressBar.setVisibility(View.GONE);
//                        makeToast("Updated");
                    }, error -> {
//                        makeToast("Error: " + error);
                    }
                );

        //Set the tag
        objectRequest.setTag(Constants.TAG);

        // Add the request to the RequestQueue.
        BaseSingleton.getInstance(this).addToRequestQueue(objectRequest);

        return !product.enabled;
    }

    //View Details
    @Override
    public void onViewDetails(int id_product, ProductListItemBinding view) {
        showDetailModal(id_product, view);
    }

    //Delete Product
    @Override
    public void onProductDelete(Product product) {
        loading(true);
        //Delete the product
        JsonObjectRequest objectRequest =
                new JsonObjectRequest(Request.Method.DELETE,
                        Constants.BASE_URL + "/" + product.id_product,
                        null,
                        response -> {
//                            makeToast("Deleted");
                            getAll();
                        }, error -> {
//                            makeToast("Error: " + error);
                        }
                );

        //Set the tag
        objectRequest.setTag(Constants.TAG);

        // Add the request to the RequestQueue.
        BaseSingleton.getInstance(this).addToRequestQueue(objectRequest);
    }

    //Edit Product
    @Override
    public void onProductEdit(Product product) {
        showPutModal(product);
    }

    // Show Detail Modal
    private void showDetailModal(int id_product, ProductListItemBinding view){
        DetailsModalBinding detailsModalBinding = DetailsModalBinding.inflate(getLayoutInflater(), null, false);
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(detailsModalBinding.getRoot());
        customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Set input disabled
        detailsModalBinding.txtName.setEnabled(false);
        detailsModalBinding.txtDescription.setEnabled(false);
        detailsModalBinding.txtPrice.setEnabled(false);
        detailsModalBinding.txtStock.setEnabled(false);

        //Set loading
        detailsModalBinding.modalLoading.setVisibility(View.VISIBLE);
        detailsModalBinding.modalContent.setVisibility(View.GONE);

        //Set card view inactivated
        view.getRoot().setActivated(false);

        //Get values
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.BASE_URL + "/" + id_product,
                response -> {
                    detailsModalBinding.modalLoading.setVisibility(View.GONE);
                    detailsModalBinding.modalContent.setVisibility(View.VISIBLE);

                    String productsJSON = null;
                    try {
                        JSONObject jsonObject = new JSONObject( response );
                        productsJSON = jsonObject.getJSONObject("data").getJSONObject("product").toString();
//                        Log.d("JSON", productsJSON);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Product product = (Product) convertData(productsJSON, false);
                    detailsModalBinding.txtName.setText(product.name);
                    detailsModalBinding.txtDescription.setText(product.description);
                    detailsModalBinding.txtPrice.setText(product.price);
                    detailsModalBinding.txtStock.setText(product.actual_stock + "");
                }, error -> {
                    detailsModalBinding.modalLoading.setVisibility(View.GONE);
                    detailsModalBinding.modalContent.setVisibility(View.VISIBLE);
                    detailsModalBinding.txtName.setText("API Disconnected");
                });

        //Set the tag
        stringRequest.setTag(Constants.TAG);

        // Add the request to the RequestQueue.
        BaseSingleton.getInstance(this).addToRequestQueue(stringRequest);

        //Set listeners
        detailsModalBinding.btnAction.setVisibility(View.GONE);
        detailsModalBinding.btnBack.setOnClickListener(v -> {
            customDialog.dismiss();
            view.getRoot().setActivated(true);
        });

        customDialog.setCancelable(true);
        customDialog.setCanceledOnTouchOutside(false);

        customDialog.show();

        //Reset swipe item
        swipeItem = null;

        //Set swipe item
        setSwipeItem();
    }

    // Show Post Modal
    private void showPostModal(){
        DetailsModalBinding detailsModalBinding = DetailsModalBinding.inflate(getLayoutInflater(), null, false);
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(detailsModalBinding.getRoot());
        customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Set listeners
        detailsModalBinding.btnAction.setOnClickListener(v -> actionProduct(customDialog, detailsModalBinding, true, null));
        detailsModalBinding.btnBack.setOnClickListener(v -> customDialog.dismiss());

        customDialog.setCancelable(true);
        customDialog.setCanceledOnTouchOutside(false);

        customDialog.show();

        //Reset swipe item
        swipeItem = null;

        //Set swipe item
        setSwipeItem();
    }

    // Show Put Modal
    private void showPutModal(Product product){
        DetailsModalBinding detailsModalBinding = DetailsModalBinding.inflate(getLayoutInflater(), null, false);
        final Dialog customDialog = new Dialog(this);
        customDialog.setContentView(detailsModalBinding.getRoot());
        customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Set values
        detailsModalBinding.txtName.setText(product.name);
        detailsModalBinding.txtDescription.setText(product.description);
        detailsModalBinding.txtPrice.setText(product.price.replace("$", ""));
        detailsModalBinding.txtStock.setText(product.actual_stock + "");

        //Set button text
        detailsModalBinding.btnAction.setText(getString(R.string.updateButton));

        //Set listeners
        detailsModalBinding.btnAction.setOnClickListener(v -> actionProduct(customDialog, detailsModalBinding, false, product));
        detailsModalBinding.btnBack.setOnClickListener(v -> customDialog.dismiss());

        customDialog.setCancelable(true);
        customDialog.setCanceledOnTouchOutside(false);

        customDialog.show();

        //Reset swipe item
        swipeItem = null;

        //Set swipe item
        setSwipeItem();
    }

    //action product
    private void actionProduct(Dialog dialog, DetailsModalBinding detailsModalBinding, Boolean isInsertAction, Product product) {
        String productName = detailsModalBinding.txtName.getText().toString();
        String productDescription = detailsModalBinding.txtDescription.getText().toString();
        String productPrice = detailsModalBinding.txtPrice.getText().toString();
        String productStock = detailsModalBinding.txtStock.getText().toString();

        if (productName.isEmpty()) {
            detailsModalBinding.txtName.setError(getString(R.string.emptyNameError));
            detailsModalBinding.txtName.requestFocus();
            return;
        }

        if (productDescription.isEmpty()) {
            detailsModalBinding.txtDescription.setError(getString(R.string.emptyDescriptionError));
            detailsModalBinding.txtDescription.requestFocus();
            return;
        }

        if (productPrice.isEmpty()) {
            detailsModalBinding.txtPrice.setError(getString(R.string.emptyPriceError));
            detailsModalBinding.txtPrice.requestFocus();
            return;
        }

        if (productStock.isEmpty()) {
            detailsModalBinding.txtStock.setError(getString(R.string.emptyStockError));
            detailsModalBinding.txtStock.requestFocus();
            return;
        }

        //RequestBody
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", productName);
            requestBody.put("description", productDescription);
            requestBody.put("price", productPrice);
            requestBody.put("actual_stock", productStock);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        if (isInsertAction) {
            //Insert a request with POST
            detailsModalBinding.progressBar.setVisibility(View.VISIBLE);
            detailsModalBinding.btnAction.setVisibility(View.GONE);
            detailsModalBinding.btnBack.setVisibility(View.INVISIBLE);
            // Request a string response from the provided URL.
            JsonObjectRequest objectRequest =
                    new JsonObjectRequest(Request.Method.POST,
                            Constants.BASE_URL,
                            requestBody,
                            response -> {
//                                makeToast("Inserted");
                                dialog.dismiss();
                                getAll();
                            }, error -> {
                                try {
                                    makeToast(decodeError(error));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                detailsModalBinding.progressBar.setVisibility(View.GONE);
                                detailsModalBinding.btnAction.setVisibility(View.VISIBLE);
                                detailsModalBinding.btnBack.setVisibility(View.VISIBLE);
                            }
                    );

            //Set the tag
            objectRequest.setTag(Constants.TAG);

            // Add the request to the RequestQueue.
            BaseSingleton.getInstance(this).addToRequestQueue(objectRequest);
        } else {
            //Update a request with PUT
            detailsModalBinding.progressBar.setVisibility(View.VISIBLE);
            detailsModalBinding.btnAction.setVisibility(View.GONE);
            detailsModalBinding.btnBack.setVisibility(View.INVISIBLE);
            // Request a string response from the provided URL.
            JsonObjectRequest objectRequest =
                    new JsonObjectRequest(Request.Method.PUT,
                            Constants.BASE_URL + "/" + product.id_product,
                            requestBody,
                            response -> {
//                                makeToast("Updated");
                                dialog.dismiss();
                                getAll();
                            }, error -> {
                                try {
                                    makeToast(decodeError(error));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                detailsModalBinding.progressBar.setVisibility(View.GONE);
                                detailsModalBinding.btnAction.setVisibility(View.VISIBLE);
                                detailsModalBinding.btnBack.setVisibility(View.VISIBLE);
                            }
                    );
            //Set the tag
            objectRequest.setTag(Constants.TAG);

            // Add the request to the RequestQueue.
            BaseSingleton.getInstance(this).addToRequestQueue(objectRequest);
        }
    }

    //Resume all requests
    /*@Override
    protected void onResume() {
        super.onResume();
        getQueue();
        if (queue != null) {
            queue.start();
        }
    }*/

    //Cancel all requests
    @Override
    protected void onStop() {
        super.onStop();
        getQueue();
        if (queue != null) {
            queue.cancelAll(Constants.TAG);
        }
    }

    //Other methods
    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Object convertData(String response, boolean isList) {
        Gson gson = new Gson();
        Type type;
        if (!isList) {
            type = new TypeToken<Product>() {
            }.getType();
        } else {
            type = new TypeToken<List<Product>>() {
            }.getType();
        }
        return gson.fromJson(response, type);
    }

    private String decodeError(com.android.volley.VolleyError error) throws JSONException, UnsupportedEncodingException {
        String responseBody = new String(error.networkResponse.data, "utf-8");
        JSONObject data = new JSONObject(responseBody).getJSONObject("error");
        return data.getString("message");
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.warnLabel.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    private void loading(boolean isLoading, boolean warnOrRecycler) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.warnLabel.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            if (warnOrRecycler) {
                binding.warnLabel.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                binding.warnLabel.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
    private void loading(boolean isLoading, boolean warnOrRecycler, String message) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.warnLabel.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            if (warnOrRecycler) {
                binding.warnLabel.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                setWarnLabel(message);
            } else {
                binding.warnLabel.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setWarnLabel(String message) {
        binding.warnLabel.setText(message);
    }

    private void setRecycler(List<Product> productList){
        // init recycler view
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set adapter
        productsAdapter = new ProductsAdapter(productList, this);
        binding.recyclerView.setAdapter(productsAdapter);
        //Reset swipe item
        swipeItem = null;

        //Set swipe item
        setSwipeItem();
    }

    private void setSwipeItem(){
        if (itemTouchHelper != null)
            itemTouchHelper.attachToRecyclerView(null);
        binding.recyclerView.clearOnChildAttachStateChangeListeners();

        if (swipeItem == null)
            swipeItem = new SwipeItem(productsAdapter, getApplicationContext());
        itemTouchHelper = new ItemTouchHelper(swipeItem);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
    }
}