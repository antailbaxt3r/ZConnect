package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.ShopCategoryItemCategory;

import java.util.Vector;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ShopCategoryRV extends RecyclerView.Adapter<ShopCategoryRV.ViewHolder> {
    Context context;
    Vector<ShopCategoryItemCategory> shopCategoryItemCategories;

    public ShopCategoryRV(Context context, Vector<ShopCategoryItemCategory> shopCategoryItemCategories) {
        this.context = context;
        this.shopCategoryItemCategories = shopCategoryItemCategories;
    }

    @Override
    public ShopCategoryRV.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.shop_category_item_format, parent, false);

        // Return a new holder instance
        return new ShopCategoryRV.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ShopCategoryRV.ViewHolder holder, int position) {
        holder.simpleDraweeView.setImageURI(Uri.parse(shopCategoryItemCategories.get(position).getImageurl()));
        holder.category.setText(shopCategoryItemCategories.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return shopCategoryItemCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView category;
        SimpleDraweeView simpleDraweeView;

        public ViewHolder(View itemView) {
            super(itemView);
            category = (TextView) itemView.findViewById(R.id.shop_category_item_format_text);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.shop_category_item_format_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShopList.class);
                    intent.putExtra("Category", shopCategoryItemCategories.get(getAdapterPosition()).getCategory());
                    context.startActivity(intent);
                    if (context instanceof ShopList) {
                        ((ShopList) context).finish();
                    }
                }
            });
        }
    }
}
