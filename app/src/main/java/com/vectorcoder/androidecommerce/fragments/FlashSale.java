package com.vectorcoder.androidecommerce.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vectorcoder.androidecommerce.R;
import com.vectorcoder.androidecommerce.adapters.ProductAdapter;
import com.vectorcoder.androidecommerce.adapters.ProductAdapterRemovable;
import com.vectorcoder.androidecommerce.app.App;
import com.vectorcoder.androidecommerce.constant.ConstantValues;
import com.vectorcoder.androidecommerce.databases.User_Recents_DB;
import com.vectorcoder.androidecommerce.models.product_model.GetAllProducts;
import com.vectorcoder.androidecommerce.models.product_model.ProductData;
import com.vectorcoder.androidecommerce.models.product_model.ProductDetails;
import com.vectorcoder.androidecommerce.network.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class FlashSale extends Fragment {

    String customerID;

    TextView emptyRecord, headerText;
    RecyclerView recents_recycler;
    List<ProductDetails> flashList;

    ProductAdapter productAdapter;
   /* User_Recents_DB recents_db = new User_Recents_DB();

    ArrayList<Integer> recents;
    List<ProductDetails> recentViewedList;*/


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.f_products_horizontal, container, false);

      /*  recents = new ArrayList<>();
        recentViewedList = new ArrayList<>();

        // Get the List of RecentlyViewed Product's IDs from the Local Databases User_Recents_DB
        recents = recents_db.getUserRecents();*/

        // Get the CustomerID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
        flashList = new ArrayList<>();

        // Binding Layout Views
        emptyRecord = (TextView) rootView.findViewById(R.id.empty_record_text);
        headerText = (TextView) rootView.findViewById(R.id.products_horizontal_header);
        recents_recycler = (RecyclerView) rootView.findViewById(R.id.products_horizontal_recycler);


        // Hide some of the Views
        emptyRecord.setVisibility(View.GONE);

        // Set text of Header
        headerText.setText(getString(R.string.flashSale));


        // Initialize the ProductAdapterRemovable and LayoutManager for RecyclerView
        productAdapter = new ProductAdapter(getContext(), flashList, true, true);
        recents_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set the Adapter and LayoutManager to the RecyclerView
        recents_recycler.setAdapter(productAdapter);

        RequestProductDetails();
    
        // Check if the recents List isn't empty
        if (flashList.size() < 1) {
            headerText.setVisibility(View.GONE);
        }
        else {
            headerText.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


    //*********** Adds Products returned from the Server to the RecentViewedList ********//

    private void addFlashProducts(ProductData productData) {

        // Add Products to recentViewedList
        if (productData.getProductData().size() > 0) {
            flashList.addAll(productData.getProductData());
        }

        // Notify the Adapter
        productAdapter.notifyDataSetChanged();
    }


    //*********** Request the Product's Details from the Server based on Product's ID ********//

    public void RequestProductDetails() {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setType("flashsale");


        Call<ProductData> call = APIClient.getInstance()
                .getAllProducts
                        (
                                getAllProducts
                        );

        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, retrofit2.Response<ProductData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Product's Details has been returned.
                        // Add Product to the recentViewedList
                        addFlashProducts(response.body());

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Product's Details haven't been returned.
                        // Call the method to process some implementations
                        addFlashProducts(response.body());

                    }
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                Toast.makeText(App.getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });

    }

}

