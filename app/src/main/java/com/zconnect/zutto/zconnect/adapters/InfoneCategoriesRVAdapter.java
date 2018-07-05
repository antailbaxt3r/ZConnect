package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.holders.InfoneCategoriesRVViewHolder;
import com.zconnect.zutto.zconnect.InfoneContactListActivity;
import com.zconnect.zutto.zconnect.ItemFormats.InfoneCategoryModel;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneCategoriesRVAdapter extends RecyclerView.Adapter<InfoneCategoriesRVViewHolder> {

    private ArrayList<InfoneCategoryModel> categoriesList;
    private Context context;

    public InfoneCategoriesRVAdapter(ArrayList<InfoneCategoryModel> categoriesList, Context context) {
        this.categoriesList = categoriesList;
        this.context = context;
    }

    @Override
    public InfoneCategoriesRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_infone_cat, parent, false);

        return new InfoneCategoriesRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoneCategoriesRVViewHolder holder, final int position) {

        holder.nametv.setText(categoriesList.get(position).getName());

        if (categoriesList.get(position).getImageurl() != null &&
                !categoriesList.get(position).getImageurl().equalsIgnoreCase("default")) {
            Uri imageUri = Uri.parse(categoriesList.get(position).getImageurl());
            holder.catImage.setImageURI(imageUri);
        }

        try{
            holder.totalContactstv.setText(categoriesList.get(position).getTotalContacts() + " members");
        }catch (Exception e){

        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentInfoneList = new Intent(context, InfoneContactListActivity.class);
                intentInfoneList.putExtra("catId", categoriesList.get(position).getCatId());
                intentInfoneList.putExtra("catName",categoriesList.get(position).getName());
                intentInfoneList.putExtra("catImageurl",categoriesList.get(position).getImageurl());
                intentInfoneList.putExtra("catAdmin",categoriesList.get(position).getAdmin());
                context.startActivity(intentInfoneList);
                CounterManager.infoneOpenCategory(categoriesList.get(position).getCatId());

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }
}
