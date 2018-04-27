package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookItem;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shubhamk on 9/2/17.
 */

public class PhonebookAdapter extends RecyclerView.Adapter<PhonebookAdapter.ViewHolder> {
    Context context;
    private Vector<PhonebookItem> phonebookItem;
    private Typeface ralewayLight;
    private Typeface ralewaySemiBold;

    public PhonebookAdapter(Vector<PhonebookItem> phonebookItem, Context context) {
        ralewayLight = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        ralewaySemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-SemiBold.ttf");
        this.phonebookItem = phonebookItem;
        this.context = context;
    }

    @Override
    public PhonebookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.view_contect_item, parent, false);

        // Return a new holder instance
        return new PhonebookAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(PhonebookAdapter.ViewHolder holder, int position) {
        holder.contactAvatarSdv.setImageURI(Uri.parse(phonebookItem.get(position).getImgurl()));
        String imageUrl = phonebookItem.get(position).getImgurl();
        if (!TextUtils.isEmpty(imageUrl)) holder.contactAvatarSdv.setImageURI(Uri.parse(imageUrl));
        holder.contactNameTv.setText(phonebookItem.get(position).getName());
        holder.contactDescTv.setText(phonebookItem.get(position).getPhonebookDisplayItem().getDesc());
    }

    @Override
    public int getItemCount() {
        return phonebookItem.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdv_avatar_contact_item)
        SimpleDraweeView contactAvatarSdv;
        @BindView(R.id.tv_name_contact_item)
        TextView contactNameTv;
        @BindView(R.id.tv_description_contact_item)
        TextView contactDescTv;
        @BindView(R.id.ib_call_contact_item)
        ImageButton callBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contactNameTv.setTypeface(ralewaySemiBold);
            contactDescTv.setTypeface(ralewayLight);

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhonebookDisplayItem phonebookDisplayItem;
                    phonebookDisplayItem = phonebookItem.get(getAdapterPosition()).getPhonebookDisplayItem();
//                    CounterManager.InfoneCallDirect(phonebookDisplayItem.getNumber());
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phonebookDisplayItem.getNumber())));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OpenUserDetail.class);
                    PhonebookDisplayItem phonebookDisplayItem;
                    phonebookDisplayItem = phonebookItem.get(getAdapterPosition()).getPhonebookDisplayItem();

                    //   CounterManager.infoneOpenContact(phonebookDisplayItem.getNumber());

                    intent.putExtra("desc", phonebookDisplayItem.getDesc());
                    intent.putExtra("name", phonebookDisplayItem.getName());
                    intent.putExtra("contactDescTv", phonebookDisplayItem.getNumber());
                    intent.putExtra("image", phonebookDisplayItem.getImageurl());
                    intent.putExtra("uid", phonebookDisplayItem.getEmail());
                    intent.putExtra("skills", phonebookDisplayItem.getSkills());
                    intent.putExtra("category", phonebookDisplayItem.getCategory());
                    intent.putExtra("Uid",phonebookDisplayItem.getUid());
                    context.startActivity(intent);
                    if (context instanceof OpenUserDetail) {
                        ((OpenUserDetail) context).finish();
                    }
                }
            });
        }
    }
}
