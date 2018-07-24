package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.AdminHome;
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.CabPoolListOfPeople;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.InfoneProfileActivity;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.Shop_detail;
import com.zconnect.zutto.zconnect.TabStoreRoom;
import com.zconnect.zutto.zconnect.TabbedEvents;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeAgo;
import com.zconnect.zutto.zconnect.addActivities.AddStatus;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.google.android.gms.internal.zzagz.runOnUiThread;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class RecentsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Vector<RecentsItemFormat> recentsItemFormats;
    private HomeActivity mHomeActivity;
    DatabaseReference mRef;
    View.OnClickListener openUserProfileListener;
    boolean flag;

    public RecentsRVAdapter(Context context, Vector<RecentsItemFormat> recentsItemFormats, HomeActivity HomeActivity) {
        this.context = context;
        this.recentsItemFormats = recentsItemFormats;
        this.mHomeActivity = HomeActivity;
    }
//
    @Override
    public int getItemViewType(int position) {
        try {
            if(recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_ADD_STATUS_STR))
            {
                return RecentTypeUtilities.KEY_RECENT_ADD_STATUS;
            }
            else if(recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_FEATURES_STR))
            {
                return RecentTypeUtilities.KEY_RECENT_FEATURES;
            }
            else if(recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR)) {
                return RecentTypeUtilities.KEY_RECENT_NORMAL_POST;
            }
            else {
                return -1;
            }
        }
        catch (NullPointerException e)
        {
            Log.d("Exception", "adding type");
            recentsItemFormats.get(position).setRecentType(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
            return RecentTypeUtilities.KEY_RECENT_NORMAL_POST;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == RecentTypeUtilities.KEY_RECENT_ADD_STATUS) {
            View addStatusView = inflater.inflate(R.layout.recents_add_status, parent, false);
            Log.d("VIEWTYPE", String.valueOf(viewType));
            return new RecentsRVAdapter.ViewHolderStatus(addStatusView);
        }
        else if(viewType == RecentTypeUtilities.KEY_RECENT_FEATURES) {
            View featuresView = inflater.inflate(R.layout.recents_features_view, parent, false);
            return new RecentsRVAdapter.FeaturesViewHolder(featuresView);
        }
        else if(viewType == RecentTypeUtilities.KEY_RECENT_NORMAL_POST) {
            View contactView = inflater.inflate(R.layout.recents_item_format, parent, false);
            return new RecentsRVAdapter.Viewholder(contactView);
        }
        else {
            View blankLayout = inflater.inflate(R.layout.row_blank_layout, parent, false);
            return new RecentsRVAdapter.BlankViewHolder(blankLayout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder2, final int position) {
        final RecentsItemFormat recentItem = recentsItemFormats.get(position);
        switch (holder2.getItemViewType())
        {
            case 0:
                ViewHolderStatus holderStatus = (ViewHolderStatus)holder2;
                break;
            case 1:
                FeaturesViewHolder featuresViewHolder = (FeaturesViewHolder) holder2;
                break;
            case 2:
                final Viewholder holder = (Viewholder)holder2;
                openUserProfileListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(UserUtilities.currentUser.getUserUID().equals(recentsItemFormats.get(position).getPostedBy().getUID()))
                        {
                            mHomeActivity.changeFragment(4);
                        }
                        else
                        {
                            Intent i = new Intent(context,OpenUserDetail.class);
                            i.putExtra("Uid",recentsItemFormats.get(position).getPostedBy().getUID());
                            context.startActivity(i);
                        }
                    }
                };
                try {
                    if (recentsItemFormats.get(position).getPostTimeMillis() > 0) {
                        TimeAgo ta = new TimeAgo(recentsItemFormats.get(position).getPostTimeMillis(), System.currentTimeMillis());
                        holder.postTime.setText(ta.calculateTimeAgo());
                    }
                    if (recentsItemFormats.get(position).getPostedBy().getUsername() != null) {
                        holder.postedBy.setText(recentsItemFormats.get(position).getPostedBy().getUsername());
                        holder.postedBy.setOnClickListener(openUserProfileListener);
                        if(recentsItemFormats.get(position).getFeature().equals("Message") && recentsItemFormats.get(position).getDesc2().equals("y")) {
                            holder.postedBy.setOnClickListener(null);
                        }
                    }
                    if (recentsItemFormats.get(position).getPostedBy().getImageThumb() != null) {
                        holder.avatarCircle.setImageURI(recentsItemFormats.get(position).getPostedBy().getImageThumb());
                        holder.avatarCircle.setOnClickListener(openUserProfileListener);
                    }
                }
                catch (Exception e) {
                    Log.d("Error Message", e.getMessage());
                }
                if(recentsItemFormats.get(position).getFeature().equals("Banner"))
                {
                    holder.prePostDetails.setVisibility(View.GONE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.VISIBLE);

                    Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.bannerImage);
//                    holder.bannerImage.setImageURI(recentsItemFormats.get(position).getImageurl());
                    holder.bannerImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.bannerLinkLayout.setVisibility(View.VISIBLE);
                            holder.bannerLinkLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(recentsItemFormats.get(position).getDesc())));

                                }
                            });
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                    }
                                    catch (InterruptedException ie)
                                    {
                                        Log.d("Interrupted Error", ie.getMessage());
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do some stuff
                                            holder.bannerLinkLayout.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            };
                            thread.start();
                        }
                    });

                }
                else if(recentsItemFormats.get(position).getFeature().equals("Infone"))
                {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.VISIBLE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);

                    holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.infone), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_people_white_18dp));
                    holder.layoutFeatureIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHomeActivity.changeFragment(3);
                        }
                    });
                    holder.postConjunction.setText(" added a ");
                    holder.post.setText("Contact");
                    holder.infoneContactName.setText(recentsItemFormats.get(position).getInfoneContactName());
                    holder.infoneContactCategory.setText(recentsItemFormats.get(position).getInfoneContactCategoryName());

                }
                else if(recentsItemFormats.get(position).getFeature().equals("Users"))
                {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.users), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_home_white_18dp));
                    holder.layoutFeatureIcon.setOnClickListener(null);
                    holder.postConjunction.setText(" just joined your community ");
                    holder.post.setVisibility(View.GONE);
                }
                else if (recentsItemFormats.get(position).getFeature().equals("Event"))
                {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.events), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_event_white_18dp));
                    holder.layoutFeatureIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, TabbedEvents.class);
                            context.startActivity(intent);
                        }
                    });
                    holder.postConjunction.setText(" created an ");
                    holder.post.setText(recentsItemFormats.get(position).getFeature());
                    holder.post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, TabbedEvents.class);
                            context.startActivity(intent);
                        }
                    });
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
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);

//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_local_mall_white_24dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
                    holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_local_mall_white_18dp));
                    holder.layoutFeatureIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, TabStoreRoom.class);
                            context.startActivity(intent);
                        }
                    });
                    holder.postConjunction.setText(" added a ");
                    holder.post.setText("Product");
                    holder.post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, TabStoreRoom.class);
                            context.startActivity(intent);
                        }
                    });
                    holder.productName.setText(recentsItemFormats.get(position).getName());
                    holder.productDesc.setText(recentsItemFormats.get(position).getDesc());
                    Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.productImage);
                    holder.productPrice.setText(recentsItemFormats.get(position).getProductPrice());
                    //set product price
                }
                else if (recentsItemFormats.get(position).getFeature().equals("CabPool"))
                {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);

                    holder.postConjunction.setText(" started a ");
                    holder.post.setText(recentsItemFormats.get(position).getFeature());
                    holder.post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CabPoolAll.class);
                            context.startActivity(intent);
                        }
                    });
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
                    holder.layoutFeatureIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CabPoolAll.class);
                            context.startActivity(intent);
                        }
                    });
                    //set text for source and destination...
                }
                else if (recentsItemFormats.get(position).getFeature().equals("Shop"))
                {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
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
                    holder.layoutFeatureIcon.setOnClickListener(null);
                }else if(recentsItemFormats.get(position).getFeature().equals("Message")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.setLike(recentsItemFormats.get(position).getKey());
                    if(recentsItemFormats.get(position).getDesc().length()<=0)
                        holder.messagesMessage.setVisibility(View.GONE);
                    else
                        holder.messagesMessage.setVisibility(View.VISIBLE);
                    try {
                        if(!recentsItemFormats.get(position).getImageurl().equals(RecentTypeUtilities.KEY_RECENTS_NO_IMAGE_STATUS) && recentsItemFormats.get(position).getImageurl()!=null){
                            holder.postImage.setVisibility(View.VISIBLE);
                            holder.postImage.setImageURI(Uri.parse(recentsItemFormats.get(position).getImageurl()));
                        }
                        else
                            holder.postImage.setVisibility(View.GONE);
                    }catch (Exception e){}

                    holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.messages), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_message_white_18dp));
                    holder.layoutFeatureIcon.setOnClickListener(null);
                    holder.postConjunction.setText(" wrote a ");
                    holder.post.setText("status");
                    holder.post.setTextColor(context.getResources().getColor(R.color.secondaryText));
                    holder.post.setTypeface(Typeface.DEFAULT);
                    holder.messagesMessage.setText(recentsItemFormats.get(position).getDesc());
                    holder.messagesMessage.setTypeface(Typeface.SANS_SERIF);
                    if(recentsItemFormats.get(position).getDesc().length()<20)
                        holder.messagesMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
                    else if(recentsItemFormats.get(position).getDesc().length()<70)
                        holder.messagesMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                    else
                        holder.messagesMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if(recentsItemFormats.get(position).getDesc2().equals("y")) {
                        holder.name.setText("Anonymous "+recentsItemFormats.get(position).getName());
                        holder.avatarCircle.setImageResource(R.drawable.question_mark_icon);
                        holder.avatarCircle.setOnClickListener(null);
//                holder.avatarCircle.setBackground(context.getResources().getDrawable(R.drawable.question_mark_icon));
                    } else {
                        //Message is not anonymous
                        holder.name.setText(recentsItemFormats.get(position).getName());
                    }
                }else if(recentsItemFormats.get(position).getFeature().equals("Forums")){
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);

                    holder.featureCircle.getBackground().setColorFilter(context.getResources().getColor(R.color.forums), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_forum_white_18dp));
                    holder.layoutFeatureIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHomeActivity.changeFragment(1);
                        }
                    });
                    holder.postConjunction.setText(" created a ");
                    holder.post.setText(recentsItemFormats.get(position).getFeature());
                    holder.forumsName.setText(recentsItemFormats.get(position).getName());
                    holder.forumCategory.setText(recentsItemFormats.get(position).getDesc());
                }
                break;
            default:
                BlankViewHolder blankViewHolder = (BlankViewHolder)holder2;
//                blankViewHolder = (BlankViewHolder)holder2;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return recentsItemFormats.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        TextView feature, name, desc;
//        SimpleDraweeView simpleDraweeView;
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
                postImage,
                productImage,
                bannerImage;
        ImageView featureIcon;
        LinearLayout infoneRecentItem, cabpoolRecentItem, eventsRecentItem, storeroomRecentItem, messagesRecentItem, forumsRecentItem, bannerRecentItem, prePostDetails;
        FrameLayout layoutFeatureIcon, bannerLinkLayout;
        //
        long statusLikeCount;
        boolean statusLikeFlag;



        public Viewholder(View itemView) {
            super(itemView);
//            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.recents_image);
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
            layoutFeatureIcon = (FrameLayout) itemView.findViewById(R.id.layout_feature_icon);
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
            postImage = (SimpleDraweeView) itemView.findViewById(R.id.messagesRecentItem_image);
            bannerRecentItem = (LinearLayout) itemView.findViewById(R.id.bannerRecentItem);
            bannerImage = (SimpleDraweeView) itemView.findViewById(R.id.bannerRecentItem_image);
            bannerLinkLayout = (FrameLayout) itemView.findViewById(R.id.bannerRecentItem_link_layout);
            prePostDetails = (LinearLayout) itemView.findViewById(R.id.prePostDetails);
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
                        i.putExtra("key",recentsItemFormats.get(getAdapterPosition()).getKey());
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
                        intent.putExtra("tab",recentsItemFormats.get(getAdapterPosition()).getId());
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

        public void setLike(final String key) {

            final DatabaseReference statusDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final RelativeLayout likeLayout = (RelativeLayout) itemView.findViewById(R.id.messagesRecentItem_like_layout);
            final ImageView likeIcon = (ImageView) itemView.findViewById(R.id.like_image_status);
            final TextView likeText = (TextView) itemView.findViewById(R.id.like_text_status);
            statusDatabase.child("likeUids").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    statusLikeCount = dataSnapshot.getChildrenCount();
                    statusDatabase.child("likeCount").setValue(dataSnapshot.getChildrenCount());

                    if(dataSnapshot.hasChild(user.getUid())){
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                        if(dataSnapshot.getChildrenCount()>0)
                            likeText.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        else
                            likeText.setText("");
                        likeText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_thumb_up_alt_white_24));
                        likeIcon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                        statusLikeFlag=true;
                    }else {
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                        if(dataSnapshot.getChildrenCount()>0)
                            likeText.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        else
                            likeText.setText("");
                        likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_white_24));
//                        likeIcon.setBackground(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_24));
                        likeIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.primaryText));
                        statusLikeFlag=false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            if (user != null) {
                likeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!statusLikeFlag){
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(user.getUid(), user.getUid());
//                            CounterManager.eventBoost(key, "Trending-Out");
                            statusDatabase.child("likeUids").updateChildren(taskMap);
                            NotificationSender notificationSender = new NotificationSender(itemView.getContext(),UserUtilities.currentUser.getUserUID());
                            NotificationItemFormat statusLikeNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_STATUS_LIKED,UserUtilities.currentUser.getUserUID());
                            statusLikeNotification.setItemKey(key);
                            statusLikeNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
//                            statusLikeNotification.setItemName(statusText);
//                            statusLikeNotification.setItemImage(statusImage);
                            statusLikeNotification.setUserName(user.getDisplayName());
                            statusLikeNotification.setCommunityName(UserUtilities.CommunityName);
                            statusLikeNotification.setItemLikeCount(statusLikeCount);

                            notificationSender.execute(statusLikeNotification);
                            Log.d("LIKESSSS", "1");

                        }else {
                            statusDatabase.child("likeUids").child(user.getUid()).removeValue();

                        }
                    }
                });

            } else {
                likeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(itemView.getContext());
                        dialog.setNegativeButton("Lite", null)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent loginIntent = new Intent(itemView.getContext(), LoginActivity.class);
                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        itemView.getContext().startActivity(loginIntent);
                                    }
                                })
                                .setTitle("Please login to boost.")
                                .create().show();
                    }
                });
            }

//            Typeface customfont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//            eventNumLit.setTypeface(customfont);
        }
    }

    class ViewHolderStatus extends RecyclerView.ViewHolder {

        LinearLayout textArea;
        SimpleDraweeView userAvatar;
        DatabaseReference mUserDetails;
        public ViewHolderStatus(View itemView) {
            super(itemView);
            mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.avatarCircle_recents_status_add);
            textArea = (LinearLayout) itemView.findViewById(R.id.text_area_recents_status_add);

            mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                    userAvatar.setImageURI(user.getImageURLThumbnail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            textArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING))){
                        Intent intent = new Intent(context, AddStatus.class);
                        context.startActivity(intent);
                    }else {
                        newUserVerificationAlert.buildAlertCheckNewUser("Add Status",context);
                    }
                }
            });

        }
    }

    private class  BlankViewHolder extends  RecyclerView.ViewHolder{

        public BlankViewHolder(View itemView) {
            super(itemView);
        }

    }

    private class FeaturesViewHolder extends RecyclerView.ViewHolder {

        HorizontalScrollView hsv;
        LinearLayout linearLayout;
        RelativeLayout events, cabpool, storeroom, admin;
        Query mOtherFeatures;

        //for other features
        SimpleDraweeView otherFeatureIcon;
        TextView featureName;
        LinearLayout otherFeatureItemLayout;
        public FeaturesViewHolder(final View itemView) {
            super(itemView);
            flag = true;
            hsv = (HorizontalScrollView) itemView.findViewById(R.id.hsv_recents_features_view);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_recents_features_view);
            events = (RelativeLayout) itemView.findViewById(R.id.events_recents_features_view);
            storeroom = (RelativeLayout) itemView.findViewById(R.id.storeroom_recents_features_view);
            cabpool = (RelativeLayout) itemView.findViewById(R.id.cabpool_recents_features_view);
            admin = (RelativeLayout) itemView.findViewById(R.id.admin_recents_features_view);
            mOtherFeatures = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("otherFeatures").orderByChild("pos");
            if(UserUtilities.currentUser.getUsername()!=null) {
                if(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)) {
                    admin.setVisibility(View.VISIBLE);
                    admin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, AdminHome.class));
                        }
                    });
                }
            }

            events.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING))){
                        Intent intent = new Intent(context, TabbedEvents.class);
                        context.startActivity(intent);
                    }else {
                        newUserVerificationAlert.buildAlertCheckNewUser("Events",context);
                    }
                }
            });

            storeroom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING))){
                        Intent intent = new Intent(context, TabStoreRoom.class);
                        context.startActivity(intent);
                    }else {
                        newUserVerificationAlert.buildAlertCheckNewUser("Storeroom",context);
                    }

                }
            });

            cabpool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING))){
                        Intent intent = new Intent(context, CabPoolAll.class);
                        context.startActivity(intent);
                    }else {
                        newUserVerificationAlert.buildAlertCheckNewUser("Cab Pool",context);
                    }

                }
            });

            mOtherFeatures.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(flag) {

                        for (final DataSnapshot shot : dataSnapshot.getChildren()) {

                            if (Integer.parseInt(shot.child("show").getValue().toString()) == 0)
                                continue;

                            otherFeatureItemLayout = (LinearLayout) View.inflate(context, R.layout.recents_features_view_item, null);
                            featureName = (TextView) otherFeatureItemLayout.findViewById(R.id.name_recents_features_view_item);
                            otherFeatureIcon = (SimpleDraweeView) otherFeatureItemLayout.findViewById(R.id.icon_recents_features_view_item);
                            otherFeatureIcon.setImageURI(shot.child("image").getValue().toString());
                            featureName.setText(shot.child("name").getValue(String.class));
                            otherFeatureItemLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING))){
                                        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shot.child("URL").getValue().toString()));
                                        context.startActivity(urlIntent);
                                    }else {
                                        newUserVerificationAlert.buildAlertCheckNewUser(shot.child("name").getValue(String.class),context);
                                    }

                                }
                            });
                            linearLayout.addView(otherFeatureItemLayout);
                        }
                        flag = false;
                    }
                    linearLayout.setVerticalScrollbarPosition(0);
                    linearLayout.setHorizontalGravity(0);
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
