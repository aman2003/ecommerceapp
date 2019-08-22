package com.vectorcoder.androidecommerce.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vectorcoder.androidecommerce.activities.MainActivity;
import com.vectorcoder.androidecommerce.R;

import java.util.ArrayList;
import java.util.List;

import com.vectorcoder.androidecommerce.adapters.CategoryListAdapter_4;
import com.vectorcoder.androidecommerce.app.App;
import com.vectorcoder.androidecommerce.models.category_model.CategoryDetails;


public class SubCategories_4 extends Fragment {

    int parentCategoryID;
    Boolean isHeaderVisible;

    TextView emptyText, headerText;
    RecyclerView category_recycler;

    CategoryListAdapter_4 categoryListAdapter;

    List<CategoryDetails> allCategoriesList;
    List<CategoryDetails> subCategoriesList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.categories, container, false);
    
        // Get CategoryID from Bundle arguments
        parentCategoryID = getArguments().getInt("CategoryID");
    
        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getArguments().getString("CategoryName", getString(R.string.actionCategory)));
    
        
        // Get CategoriesList from ApplicationContext
        allCategoriesList = new ArrayList<>();
        allCategoriesList = ((App) getContext().getApplicationContext()).getCategoriesList();


        // Binding Layout Views
        emptyText = (TextView) rootView.findViewById(R.id.empty_record_text);
        headerText = (TextView) rootView.findViewById(R.id.categories_header);
        category_recycler = (RecyclerView) rootView.findViewById(R.id.categories_recycler);


        // Hide some of the Views
        headerText.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);


        subCategoriesList = new ArrayList<>();

        for (int i=0;  i<allCategoriesList.size();  i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase(String.valueOf(parentCategoryID))) {
                subCategoriesList.add(allCategoriesList.get(i));
            }
        }
    
    
        // Initialize the CategoryListAdapter and LayoutManager for RecyclerView
        categoryListAdapter = new CategoryListAdapter_4(getContext(), subCategoriesList, true);
        category_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    
        // Set the Adapter to the RecyclerView
        category_recycler.setAdapter(categoryListAdapter);

        
        categoryListAdapter.notifyDataSetChanged();


        return rootView;
    }

}

