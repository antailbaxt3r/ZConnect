package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.ShopDetailsItem;
import com.zconnect.zutto.zconnect.ItemFormats.ShopListItem;

import java.util.Vector;

/**
 * Created by shubhamk on 8/2/17.
 */

public class ShopListRV extends RecyclerView.Adapter<ShopListRV.ViewHolder> {
    Context context;
    Vector<ShopListItem> shopListItem;

    public ShopListRV(Context context, Vector<ShopListItem> shopListItem) {
        this.context = context;
        this.shopListItem = shopListItem;
    }

    @Override
    public ShopListRV.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.shop_list_item_format, parent, false);

        // Return a new holder instance
        return new ShopListRV.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ShopListRV.ViewHolder holder, int position) {
        holder.simpleDraweeView.setImageURI(Uri.parse(shopListItem.get(position).getImageurl()));
        holder.textView.setText(shopListItem.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return shopListItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        SimpleDraweeView simpleDraweeView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.shop_list_item_format_text);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.shop_list_item_format_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShopDetailsItem shopDetailsItem;
                    shopDetailsItem = shopListItem.get(getAdapterPosition()).getShopDetailsItem();
                    Intent intent = new Intent(context, Shop_detail.class);
                    intent.putExtra("Name", shopDetailsItem.getName());
                    intent.putExtra("Details", shopDetailsItem.getDetails());
                    intent.putExtra("Imageurl", shopDetailsItem.getImageurl());
                    intent.putExtra("Menu", shopDetailsItem.getMenuurl());
                    intent.putExtra("Lat", shopDetailsItem.getLat());
                    intent.putExtra("Lon", shopDetailsItem.getLon());
                    intent.putExtra("Number", shopDetailsItem.getNumber());
                    intent.putExtra("ShopId", shopDetailsItem.getShopid());

                    CounterManager.shopDetails(shopDetailsItem.getName());
                    context.startActivity(intent);
                    if (context instanceof Shop_detail) {
                        ((Shop_detail) context).finish();
                    }
                }
            });

            //changing fonts
            Typeface customFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            textView.setTypeface(customFont);
        }
    }
}
