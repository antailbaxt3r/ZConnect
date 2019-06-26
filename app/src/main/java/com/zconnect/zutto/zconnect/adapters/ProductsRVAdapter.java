package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.ProductsViewHolder;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;

import java.util.Vector;

/**
 * Created by Lokesh Garg on 28-03-2018.
 */

public class ProductsRVAdapter extends RecyclerView.Adapter<ProductsViewHolder>{

    Vector<Product> productVector;
    Context ctx;
    private DatabaseReference StoreRoomRef;

    public ProductsRVAdapter(Vector<Product> productVector,Context ctx,DatabaseReference store) {
        this.productVector=productVector;
        this.ctx = ctx;
        this.StoreRoomRef = store;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == ProductUtilities.TYPE_ADD)
            view = layoutInflater.inflate(R.layout.products_row, parent, false);
        else
            view = layoutInflater.inflate(R.layout.ask_products_row, parent, false);
        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductsViewHolder holder, final int position) {
        try {
            StoreRoomRef.child(productVector.get(position).getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("NumberOfViews"))
                        holder.setNumberOfViewsInHolder(productVector.get(position).getNumberOfViews());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e){
            Log.e("Error Message","Null reference to Database");
        }

        if(getItemViewType(position) == ProductUtilities.TYPE_ADD)
        {
            holder.setPrice(productVector.get(position).getPrice());
            holder.setNegotiable(productVector.get(position).getIsNegotiable());
        }
        else
        {
            holder.setAskText(productVector.get(position).getProductName());
        }
        if(productVector.get(position).getImage()!=null)
        {
            holder.setImage(ctx,productVector.get(position).getImage());
            if(getItemViewType(position)==ProductUtilities.TYPE_ASK)
                holder.hideAskText(); }
        else
        {
            holder.setImage(ctx,null);
        }
        holder.setProductName(productVector.get(position).getProductName());
        holder.defaultSwitch(productVector.get(position).getKey(),ctx,productVector.get(position).getCategory(),productVector.get(position).getProductName());
        holder.openProduct(productVector.get(position).getKey(), productVector.get(position).getType());
        holder.setProductDate(productVector.get(position).getPostTimeMillis(), System.currentTimeMillis());
    }


    @Override
    public int getItemCount() {
        return productVector.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(productVector.get(position).getType()!=null && productVector.get(position).getType().equals(ProductUtilities.TYPE_ASK_STR))
            return ProductUtilities.TYPE_ASK;
        else
            return ProductUtilities.TYPE_ADD;
    }
}
