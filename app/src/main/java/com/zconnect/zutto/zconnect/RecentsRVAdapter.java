package com.zconnect.zutto.zconnect;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.Utilities.DrawableUtilities;
import com.zconnect.zutto.zconnect.Utilities.TimeAgo;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

public class RecentsRVAdapter extends RecyclerView.Adapter<RecentsRVAdapter.ViewHolder> {

    Context context;
    Vector<RecentsItemFormat> recentsItemFormats;
    private HomeActivity mHomeActivity;
    DatabaseReference mRef;

    public RecentsRVAdapter(Context context, Vector<RecentsItemFormat> recentsItemFormats, HomeActivity HomeActivity) {
        this.context = context;
        this.recentsItemFormats = recentsItemFormats;
        mHomeActivity = HomeActivity;
    }

    @Override
    public RecentsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recents_item_format, parent, false);
        return new RecentsRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RecentsRVAdapter.ViewHolder holder, final int position) {

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
                holder.postedBy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,OpenUserDetail.class);
                        i.putExtra("Uid",recentsItemFormats.get(position).getPostedBy().getUID());
                        context.startActivity(i);
                    }
                });
                if(recentsItemFormats.get(position).getFeature().equals("Message") && recentsItemFormats.get(position).getDesc2().equals("y")) {
                    holder.postedBy.setOnClickListener(null);
                }
            }
            if (recentsItemFormats.get(position).getPostedBy().getImageThumb() != null) {
                holder.avatarCircle.setImageURI(recentsItemFormats.get(position).getPostedBy().getImageThumb());
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
            holder.messagesRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);

            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.infone), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_people_white_18dp));
            holder.postConjunction.setText(" added a ");
            holder.post.setText("Contact");
            holder.infoneContactName.setText(recentsItemFormats.get(position).getInfoneContactName());
            holder.infoneContactCategory.setText(recentsItemFormats.get(position).getInfoneContactCategoryName());

        }
        else if(recentsItemFormats.get(position).getFeature().equals("Users"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.messagesRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);

            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.users), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_home_white_18dp));
            holder.postConjunction.setText(" just joined your community ");
            holder.post.setVisibility(View.GONE);
        }
        else if (recentsItemFormats.get(position).getFeature().equals("Event"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.messagesRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.VISIBLE);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.events), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_event_white_18dp));
            holder.postConjunction.setText(" created an ");
            holder.post.setText(recentsItemFormats.get(position).getFeature());
            holder.eventName.setText(recentsItemFormats.get(position).getName());
            try {
                Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(recentsItemFormats.get(position).getDesc2());
                holder.eventDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(date));
            }
            catch (ParseException pe) {
                Log.d("Error Alert ", pe.getMessage());
                holder.eventDate.setText(recentsItemFormats.get(position).getDesc2());
            }
            holder.eventDesc.setText(recentsItemFormats.get(position).getDesc());
            Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.eventImage);
        }
        else if (recentsItemFormats.get(position).getFeature().equals("StoreRoom"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.messagesRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.VISIBLE);

//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_local_mall_white_24dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_local_mall_white_18dp));
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
            holder.messagesRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);
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
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_local_taxi_white_18dp));
            //set text for source and destination...
        }
        else if (recentsItemFormats.get(position).getFeature().equals("Shop"))
        {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);
            holder.messagesRecentItem.setVisibility(View.GONE);
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.shops), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_store_white_18dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.shops), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_store_white_18dp));
            holder.postConjunction.setText(" put an ");
            holder.post.setText("Offer");
        }else if(recentsItemFormats.get(position).getFeature().equals("Message")) {
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.GONE);
            holder.messagesRecentItem.setVisibility(View.VISIBLE);

            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.messages), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_message_white_18dp));
            holder.postConjunction.setText(" posted a ");
            holder.post.setText(recentsItemFormats.get(position).getFeature());
            holder.messagesMessage.setText(recentsItemFormats.get(position).getDesc());
            if(recentsItemFormats.get(position).getDesc2().equals("y")) {
                holder.name.setText("Anonymous "+recentsItemFormats.get(position).getName());
                holder.avatarCircle.setImageResource(R.drawable.question_mark_icon);
//                holder.avatarCircle.setBackground(context.getResources().getDrawable(R.drawable.question_mark_icon));
            } else {
                //Message is not anonymous
                holder.name.setText(recentsItemFormats.get(position).getName());
            }
        }else if(recentsItemFormats.get(position).getFeature().equals("Forums")){
            holder.infoneRecentItem.setVisibility(View.GONE);
            holder.eventsRecentItem.setVisibility(View.GONE);
            holder.storeroomRecentItem.setVisibility(View.GONE);
            holder.cabpoolRecentItem.setVisibility(View.GONE);
            holder.messagesRecentItem.setVisibility(View.GONE);
            holder.forumsRecentItem.setVisibility(View.VISIBLE);

            holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.forums), PorterDuff.Mode.SRC_ATOP);
            holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_forum_white_18dp));
            holder.postConjunction.setText(" created a ");
            holder.post.setText(recentsItemFormats.get(position).getFeature());
            holder.forumsName.setText(recentsItemFormats.get(position).getName());
            holder.forumCategory.setText(recentsItemFormats.get(position).getDesc());
        }

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
                productName, productPrice, productDesc,
                messagesMessage,
                forumsName, forumCategory;
        SimpleDraweeView featureCircle, avatarCircle,
                eventImage,
                productImage;
        ImageView featureIcon;
        LinearLayout infoneRecentItem, cabpoolRecentItem, eventsRecentItem, storeroomRecentItem, messagesRecentItem, forumsRecentItem;
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
            featureIcon = (ImageView) itemView.findViewById(R.id.recents_featIcon);
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
            messagesRecentItem = (LinearLayout) itemView.findViewById(R.id.messagesRecentItem);
            messagesMessage = (TextView) itemView.findViewById(R.id.messagesRecentItem_message);
            forumsRecentItem = (LinearLayout) itemView.findViewById(R.id.forumsRecentItem);
            forumsName = (TextView) itemView.findViewById(R.id.forumsRecentItem_name);
            forumCategory = (TextView) itemView.findViewById(R.id.forumsRecentItem_category);
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
                              i = new Intent(context, OpenProductDetails.class);
                              i.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getId());
                              context.startActivity(i);

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
                        i=new Intent(context,CabPoolListOfPeople.class);
                        Log.e("check","executed");
                        i.putExtra("key",recentsItemFormats.get(getAdapterPosition()).getId());
                        i.putExtra("date",recentsItemFormats.get(getAdapterPosition()).getDT());
                        context.startActivity(i);
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Message")) {
                        i=new Intent(context,ChatActivity.class);

                        mRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home/"+recentsItemFormats.get(getAdapterPosition()).getKey());
                        i.putExtra("ref",mRef.toString());
                        i.putExtra("type","post");
                        context.startActivity(i);
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Infone")){
                        i = new Intent(context,InfoneProfileActivity.class);
                        i.putExtra("infoneUserId",recentsItemFormats.get(getAdapterPosition()).getId());
                        context.startActivity(i);
                    } else if(recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Forums")){
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("type","forums");
                        intent.putExtra("key",recentsItemFormats.get(getAdapterPosition()).getKey());
                        intent.putExtra("name",recentsItemFormats.get(getAdapterPosition()).getName());
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(recentsItemFormats.get(getAdapterPosition()).getKey()).toString());
                        context.startActivity(intent);
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
