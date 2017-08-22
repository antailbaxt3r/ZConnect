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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.RecentsItemFormat;

import java.util.Vector;

/**
 * Created by shubhamk on 20/3/17.
 */

public class RecentsRVAdapter extends RecyclerView.Adapter<RecentsRVAdapter.ViewHolder> {

    Context context;
    Vector<RecentsItemFormat> recentsItemFormats;
    private HomeActivity mHomeActivity;

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
    public void onBindViewHolder(RecentsRVAdapter.ViewHolder holder, int position) {
        holder.simpleDraweeView.setImageURI(Uri.parse(recentsItemFormats.get(position).getImageurl()));
        holder.feature.setText(recentsItemFormats.get(position).getFeature());
        holder.name.setText(recentsItemFormats.get(position).getName());
        holder.desc.setText(recentsItemFormats.get(position).getDesc());
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

        public ViewHolder(View itemView) {
            super(itemView);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.recents_image);
            name = (TextView) itemView.findViewById(R.id.recentname);
            feature = (TextView) itemView.findViewById(R.id.featurename);
            desc = (TextView) itemView.findViewById(R.id.recentdesc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Event")) {
                        i = new Intent(context, OpenEventDetail.class);
                        i.putExtra("id", recentsItemFormats.get(getAdapterPosition()).getId());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Event").child("VerifiedPosts").child(recentsItemFormats.get(getAdapterPosition()).getId());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("EventName").getValue() != null)
                                    nam = dataSnapshot.child("EventName").getValue().toString();
                                if (nam != null)
                                    context.startActivity(i);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //context.startActivity(i);
                        mHomeActivity.finish();
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("StoreRoom")) {
                        Intent intent = new Intent(context, TabStoreRoom.class);
                        context.startActivity(intent);
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Shop")) {
                        final Intent intent = new Intent(context, Shop_detail.class);
                        intent.putExtra("ShopId", recentsItemFormats.get(getAdapterPosition()).getId());
                        intent.putExtra("Tag", "1");
                        intent.putExtra("Imageurl", recentsItemFormats.get(getAdapterPosition()).getDesc2());
                        //  Log.v("im1",recentsItemFormats.get(getAdapterPosition()).getDesc2());
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
