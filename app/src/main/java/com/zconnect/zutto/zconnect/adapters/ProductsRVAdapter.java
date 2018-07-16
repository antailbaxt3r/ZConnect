package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.ProductsViewHolder;

import java.util.Vector;

/**
 * Created by Lokesh Garg on 28-03-2018.
 */

public class ProductsRVAdapter extends RecyclerView.Adapter<ProductsViewHolder>{

    Vector<Product> productVector;
    Context ctx;

    public ProductsRVAdapter(Vector<Product> productVector,Context ctx) {
        this.productVector=productVector;
        this.ctx = ctx;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.products_row, parent, false);

        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, int position) {
        holder.setImage(ctx,productVector.get(position).getImage());
        holder.setPrice(productVector.get(position).getPrice(),productVector.get(position).getNegotiable());
        holder.setProductName(productVector.get(position).getProductName());
//        holder.setSellerNumber(productVector.get(position).getCategory(),productVector.get(position).getPhone_no(),ctx);
        holder.defaultSwitch(productVector.get(position).getKey(),ctx,productVector.get(position).getCategory(),productVector.get(position).getProductName());
        holder.openProduct(productVector.get(position).getKey());
    }


    @Override
    public int getItemCount() {
        return productVector.size();
    }


}
