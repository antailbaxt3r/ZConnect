package com.zconnect.zutto.zconnect;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.time.Clock;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.Utilities.TimeAgo;

import java.util.List;
import java.util.Vector;

/**
 * Created by shubhamk on 20/3/17.
 */

@TargetApi(21)public class RecentsRVAdapter extends RecyclerView.Adapter<RecentsRVAdapter.ViewHolder> {

    Context context;
    Vector<RecentsItemFormat> recentsItemFormats;
    private HomeActivity mHomeActivity;
    List<String> storeroomProductList;
    DatabaseReference mRef;

    public RecentsRVAdapter(Context context, Vector<RecentsItemFormat> recentsItemFormats, HomeActivity HomeActivity,List<String> storeroomProductList) {
        this.context = context;
        this.recentsItemFormats = recentsItemFormats;
        mHomeActivity = HomeActivity;
        this.storeroomProductList = storeroomProductList;
    }

    @Override
    public RecentsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recents_item_format, parent, false);
        return new RecentsRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RecentsRVAdapter.ViewHolder holder, int position) {

//        holder.desc.setText(recentsItemFormats.get(position).getDesc());
//<<<<<<< HEAD
        //new ui
        try {
            if (recentsItemFormats.get(position).getPostTimeMillis() > 0) {
                TimeAgo ta = new TimeAgo(recentsItemFormats.get(position).getPostTimeMillis(), System.currentTimeMillis());
                holder.postTime.setText(ta.calculateTimeAgo());
            }
            if (recentsItemFormats.get(position).getPostedBy().getUsername() != null) {
                holder.postedBy.setText(recentsItemFormats.get(position).getPostedBy().getUsername());
            }
        }
        catch (Exception e) {

        }
        if(recentsItemFormats.get(position).getFeature().equals("Infone"))
        {
            holder.infoneRecentItem.setVisibility(View.VISIBLE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.infone), PorterDuff.Mode.SRC_ATOP);
            holder.postConjunction.setText(" added a ");
            holder.post.setText("Contact");
            holder.infoneContactName.setText(recentsItemFormats.get(position).getInfoneContactName());
            holder.infoneContactCategory.setText(recentsItemFormats.get(position).getInfoneContactCategory());

        }
        else if(recentsItemFormats.get(position).getFeature().equals("Users"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.infone), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_people_white_24dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.infone), PorterDuff.Mode.SRC_ATOP);
            holder.postConjunction.setText(" just joined your community. ");
            holder.post.setText(recentsItemFormats.get(position).getFeature());
        }
        else if (recentsItemFormats.get(position).getFeature().equals("Event"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.VISIBLE);
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.events), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_event_white_18dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.events), PorterDuff.Mode.SRC_ATOP);
            holder.postConjunction.setText(" created an ");
            holder.post.setText(recentsItemFormats.get(position).getFeature());
            holder.eventName.setText(recentsItemFormats.get(position).getName());
            holder.eventDesc.setText(recentsItemFormats.get(position).getDesc());
            Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.eventImage);
        }
        else if (recentsItemFormats.get(position).getFeature().equals("StoreRoom"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.VISIBLE);
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_local_mall_white_24dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
            holder.postConjunction.setText(" added a ");
            holder.post.setText("Product");
            holder.productName.setText(recentsItemFormats.get(position).getName());
            holder.productDesc.setText(recentsItemFormats.get(position).getDesc());
            Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.productImage);
            holder.productPrice.setText(recentsItemFormats.get(position).getProductPrice());
            //set product price
        }
        else if (recentsItemFormats.get(position).getFeature().equals("CabPool"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.VISIBLE);
            holder.postConjunction.setText(" started a ");
            holder.post.setText(recentsItemFormats.get(position).getFeature());
            holder.cabpoolSource.setText(recentsItemFormats.get(position).getCabpoolSource());
            holder.cabpoolDestination.setText(recentsItemFormats.get(position).getCabpoolDestination());
            holder.cabpoolDate.setText(recentsItemFormats.get(position).getCabpoolDate());
            holder.cabpoolTime.setText(recentsItemFormats.get(position).getCabpoolTime());
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.cabpool), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_local_taxi_white_18dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.cabpool), PorterDuff.Mode.SRC_ATOP);
            //set text for source and destination...
        }
        else if (recentsItemFormats.get(position).getFeature().equals("Shop"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.shops), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_store_white_18dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.shops), PorterDuff.Mode.SRC_ATOP);
            holder.postConjunction.setText(" put an ");
            holder.post.setText("Offer");
        }
        //
//=======
        if(recentsItemFormats.get(position).getFeature().equals("Message")&&recentsItemFormats.get(position).getDesc2().equals("y")) {
            //Message is anonymous
            holder.name.setText("Anonymous "+recentsItemFormats.get(position).getName());
        } else {
            //Message is not anonymous
            holder.name.setText(recentsItemFormats.get(position).getName());
        }

//>>>>>>> master
    }

    @Override
    public int getItemCount() {
        return recentsItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView feature, name, desc;
        SimpleDraweeView simpleDraweeView;
        Intent i;
        String nam;

        //new ui
        TextView postedBy, postConjunction, post, postTime,
                infoneContactName, infoneContactCategory,
                cabpoolSource, cabpoolDestination, cabpoolDate, cabpoolTime,
                eventName, eventDate, eventDesc,
                productName, productPrice, productDesc;
        SimpleDraweeView featureCircle, avatarCircle,
                eventImage,
                productImage;
        LinearLayout infoneRecentItem, cabpoolRecentItem, eventsRecentItem, storeroomRecentItem;
        //



        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.recents_image);
            name = (TextView) itemView.findViewById(R.id.recentname);
            feature = (TextView) itemView.findViewById(R.id.featurename);
            desc = (TextView) itemView.findViewById(R.id.recentdesc);

            //new ui
            postedBy = (TextView) itemView.findViewById(R.id.postedBy);
            postConjunction = (TextView) itemView.findViewById(R.id.postConjunction);
            post = (TextView) itemView.findViewById(R.id.post);
            postTime = (TextView) itemView.findViewById(R.id.postTime);
            featureCircle = (SimpleDraweeView) itemView.findViewById(R.id.featureCircle);
            avatarCircle = (SimpleDraweeView) itemView.findViewById(R.id.avatarCircle);
            infoneRecentItem = (LinearLayout) itemView.findViewById(R.id.infoneRecentItem);
            infoneContactName = (TextView) itemView.findViewById(R.id.infoneName);
            infoneContactCategory = (TextView) itemView.findViewById(R.id.infoneCategory);
            cabpoolRecentItem = (LinearLayout) itemView.findViewById(R.id.cabpoolRecentItem);
            cabpoolSource = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_source);
            cabpoolDestination = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_destination);
            cabpoolDate = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_date);
            cabpoolTime = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_time);
            eventsRecentItem = (LinearLayout) itemView.findViewById(R.id.eventsRecentItem);
            eventName = (TextView) itemView.findViewById(R.id.eventsRecentItem_name);
            eventDate = (TextView) itemView.findViewById(R.id.eventsRecentItem_date);
            eventDesc = (TextView) itemView.findViewById(R.id.eventsRecentItem_description);
            eventImage = (SimpleDraweeView) itemView.findViewById(R.id.eventsRecentItem_image);
            storeroomRecentItem = (LinearLayout) itemView.findViewById(R.id.storeroomRecentItem);
            productName = (TextView) itemView.findViewById(R.id.storeroomRecentItem_name);
            productPrice = (TextView) itemView.findViewById(R.id.storeroomRecentItem_price);
            productDesc = (TextView) itemView.findViewById(R.id.storeroomRecentItem_description);
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.storeroomRecentItem_image);
            //

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Event")) {
                        i = new Intent(context, OpenEventDetail.class);
                        try {
                            i.putExtra("id", recentsItemFormats.get(getAdapterPosition()).getId());
                            context.startActivity(i);
                        }catch (Exception e) {
                            Log.d("Error Alert: ", e.getMessage());
                        }

                        //context.startActivity(i);
                        //mHomeActivity.finish();
                        //mHome.finish();
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("StoreRoom")) {
                          try{
                              if (storeroomProductList.contains(recentsItemFormats.get(getAdapterPosition()).getId())) {
                                  i = new Intent(context, OpenProductDetails.class);
                                  i.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getId());
                                  context.startActivity(i);
                              }else {
                                  Toast.makeText(view.getContext(), "Product Already Sold", Toast.LENGTH_SHORT).show();
                              }
                          } catch(Exception e) {
                              Log.d("Error Alert: ", e.getMessage());
                            }
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Shop")) {
                        try {
                            i = new Intent(context, Shop_detail.class);
                            i.putExtra("ShopId", recentsItemFormats.get(getAdapterPosition()).getId());
                            i.putExtra("Name", recentsItemFormats.get(getAdapterPosition()).getName());
                            i.putExtra("Imageurl", recentsItemFormats.get(getAdapterPosition()).getImageurl());
                            //  Log.v("im1",recentsItemFormats.get(getAdapterPosition()).getDesc2());
                            context.startActivity(i);
                        }catch (Exception e) {
                            Log.d("Error Alert: ", e.getMessage());
                        }
                    }else if(recentsItemFormats.get(getAdapterPosition()).getFeature().equals("CabPool")){
                        i=new Intent(context,CabListOfPeople.class);
                        Log.e("check","executed");
                        i.putExtra("key",recentsItemFormats.get(getAdapterPosition()).getId());
                        i.putExtra("date",recentsItemFormats.get(getAdapterPosition()).getDT());
                        context.startActivity(i);
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Message")) {
                        i=new Intent(context,ChatActivity.class);
                        mRef = FirebaseDatabase.getInstance().getReference().child("home/"+recentsItemFormats.get(getAdapterPosition()).getKey());
                        i.putExtra("ref",mRef.toString());
                        context.startActivity(i);
                    }
                }
            });

            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            Typeface quicksandMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-ExtraLight.ttf");
            name.setTypeface(quicksandMedium);
            feature.setTypeface(quicksandBold);
            desc.setTypeface(quicksandLight);
        }
    }
}
