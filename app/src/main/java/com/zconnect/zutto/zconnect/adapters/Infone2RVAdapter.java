package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.holders.Infone2RVViewHolder;
import com.zconnect.zutto.zconnect.InfoneContactListActivity;
import com.zconnect.zutto.zconnect.ItemFormats.Infone2CategoryModel;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;

/**
 * Created by tanmay on 24/3/18.
 */

public class Infone2RVAdapter extends RecyclerView.Adapter<Infone2RVViewHolder> {

    private ArrayList<Infone2CategoryModel> categoriesList;
    private Context context;

    public Infone2RVAdapter(ArrayList<Infone2CategoryModel> categoriesList, Context context) {
        this.categoriesList = categoriesList;
        this.context = context;
    }

    @Override
    public Infone2RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_infone_cat, parent, false);

        return new Infone2RVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Infone2RVViewHolder holder, final int position) {

        holder.nametv.setText(categoriesList.get(position).getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentInfoneList=new Intent(context,InfoneContactListActivity.class);
                intentInfoneList.putExtra("category",categoriesList.get(position).getName());
                context.startActivity(intentInfoneList);

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }
}
