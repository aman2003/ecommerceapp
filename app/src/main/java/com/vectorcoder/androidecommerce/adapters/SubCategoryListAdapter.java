package com.vectorcoder.androidecommerce.adapters;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.vectorcoder.androidecommerce.activities.MainActivity;
import com.vectorcoder.androidecommerce.R;

import java.util.List;

import com.vectorcoder.androidecommerce.constant.ConstantValues;
import com.vectorcoder.androidecommerce.fragments.Products;
import com.vectorcoder.androidecommerce.models.category_model.CategoryDetails;


/**
 * SubCategoryListAdapter is the adapter class of ListView holding List of SubCategories in CategoriesList_6
 **/

public class SubCategoryListAdapter extends BaseAdapter {

    Context context;
    private List<CategoryDetails> subCategoriesList;
    
    private LayoutInflater layoutInflater;


    public SubCategoryListAdapter(Context context, List<CategoryDetails> subCategoriesList) {
        this.context = context;
        this.subCategoriesList = subCategoriesList;

        layoutInflater = LayoutInflater.from(context);
    }
    
    
    //********** Returns the total number of items in the data set represented by this Adapter *********//
    
    @Override
    public int getCount() {
        return subCategoriesList.size();
    }
    
    
    //********** Returns the item associated with the specified position in the data set *********//
    
    @Override
    public Object getItem(int position) {
        return subCategoriesList.get(position);
    }
    
    
    //********** Returns the item id associated with the specified position in the list *********//
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    
    //********** Returns a View that displays the data at the specified position in the data set *********//
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_sub_categories_6, parent, false);

            holder = new ViewHolder();

            holder.sub_category_layout = (RelativeLayout) convertView.findViewById(R.id.sub_category_layout);
            holder.sub_category_image = (ImageView) convertView.findViewById(R.id.sub_category_image);
            holder.sub_category_title = (TextView) convertView.findViewById(R.id.sub_category_title);
            holder.sub_category_products = (TextView) convertView.findViewById(R.id.sub_category_products);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        holder.sub_category_title.setText(subCategoriesList.get(position).getName());
        holder.sub_category_products.setText(subCategoriesList.get(position).getTotalProducts() + " "+ context.getString(R.string.products));

        Glide.with(context)
                .load(ConstantValues.ECOMMERCE_URL+subCategoriesList.get(position).getImage())
                .error(R.drawable.placeholder)
                .into(holder.sub_category_image);


        holder.sub_category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Category Info
                Bundle categoryInfo = new Bundle();
                categoryInfo.putInt("CategoryID", Integer.parseInt(subCategoriesList.get(position).getId()));
                categoryInfo.putString("CategoryName", subCategoriesList.get(position).getName());

                // Navigate to Products Fragment
                Fragment fragment = new Products();
                fragment.setArguments(categoryInfo);
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });



        return convertView;
    }
    
    
    
    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/
    
    static class ViewHolder {
        ImageView sub_category_image;
        RelativeLayout sub_category_layout;
        TextView sub_category_title, sub_category_products;
    }

}

