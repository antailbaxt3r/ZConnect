package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.ShopOfferItemFormat;

import java.util.Vector;

/**
 * Created by shubhamk on 9/4/17.
 */

public class ShopOfferRV extends RecyclerView.Adapter<ShopOfferRV.ViewHolder> {
    Context context;
    Vector<ShopOfferItemFormat> shopOfferItemFormats;

    public ShopOfferRV(Context context, Vector<ShopOfferItemFormat> shopOfferItemFormats) {
        this.context = context;
        this.shopOfferItemFormats = shopOfferItemFormats;
    }

    @Override
    public ShopOfferRV.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.shop_offer_item_format, parent, false);

        // Return a new holder instance
        return new ShopOfferRV.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ShopOfferRV.ViewHolder holder, int position) {
        holder.title.setText(shopOfferItemFormats.get(position).getName());
        holder.desc.setText(shopOfferItemFormats.get(position).getDesc());
        holder.simpleDraweeView.setImageURI(Uri.parse(shopOfferItemFormats.get(position).getImage()));
    }

    @Override
    public int getItemCount() {
        return shopOfferItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc;
        SimpleDraweeView simpleDraweeView;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.offerTitle);
            desc = (TextView) itemView.findViewById(R.id.offerDescription);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.offerImage);

            //changing fonts
            Typeface customFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Raleway-Regular.ttf");
            Typeface customFont2 = Typeface.createFromAsset(itemView.getContext().getAssets(), "font/Raleway-Light.ttf");
            title.setTypeface(customFont);
            desc.setTypeface(customFont2);
        }
    }
}
