package com.zconnect.zutto.zconnect;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;


public class CategoriesTab extends Fragment {

    GridView category;

    public CategoriesTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_tab, container, false);
        category = (GridView) view.findViewById(R.id.category_grid);
        category.setAdapter(new CategoryAdapter(getContext()));
//        category.setOnItemClickListener(this);

        return view;
    }
}

class EachCategory {
    int categoryIcon;
    String categoryName;

    EachCategory(int categoryIcon, String categoryName) {
        this.categoryIcon = categoryIcon;
        this.categoryName = categoryName;
    }
}

class viewHolder {
    ImageView categoryImage;

    viewHolder(View view) {
        categoryImage = (ImageView) view.findViewById(R.id.categoryImage);
    }

}

class CategoryAdapter extends BaseAdapter {

    ArrayList<EachCategory> list;
    Context context;

    CategoryAdapter(Context context) {
        this.context = context;
        list = new ArrayList<EachCategory>();
        Resources res = context.getResources();
        String[] categoriesNames = res.getStringArray(R.array.categories);
        int[] categoriesIcons = {R.drawable.electronics, R.drawable.speakers, R.drawable.storage, R.drawable.books, R.drawable.roomnecessities, R.drawable.novels, R.drawable.others};
        for (int i = 0; i < 7; i++) {
            EachCategory tempCategory = new EachCategory(categoriesIcons[i], categoriesNames[i]);
            list.add(tempCategory);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        viewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_category, viewGroup, false);
            holder = new viewHolder(row);
            row.setTag(holder);
        } else {

            holder = (viewHolder) row.getTag();
        }
        final EachCategory temp = list.get(i);
        holder.categoryImage.setImageResource(temp.categoryIcon);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), IndividualCategory.class);
                intent.putExtra("Category", temp.categoryName);
                context.startActivity(intent);

            }
        });
        return row;

    }
}
