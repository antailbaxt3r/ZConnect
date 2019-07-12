package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.InfoneContactsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;

import java.util.ArrayList;
import java.util.List;

public class AllUsersRVAdapter extends RecyclerView.Adapter<InfoneContactsRVViewHolder> implements Filterable {
    interface OnItemCheckListener {
        boolean onItemCheck(InfoneContactsRVItem item);

        boolean onItemUncheck(InfoneContactsRVItem item);
    }


    @NonNull
    private OnItemCheckListener onItemCheckListener;

    Context context;
    ArrayList<InfoneContactsRVItem> infoneContactsRVItems = new ArrayList<InfoneContactsRVItem>();
    ArrayList<InfoneContactsRVItem> filteredInfoneContactsRVItems = new ArrayList<InfoneContactsRVItem>();


    public AllUsersRVAdapter(ArrayList<InfoneContactsRVItem> infoneContactsRVItems, @NonNull OnItemCheckListener onItemCheckListener){
        this.infoneContactsRVItems = infoneContactsRVItems;
        this.onItemCheckListener = onItemCheckListener;
        this.filteredInfoneContactsRVItems = infoneContactsRVItems;
    }


    @Override
    public InfoneContactsRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_infone_contacts, parent, false);

        return new InfoneContactsRVViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final InfoneContactsRVViewHolder holder, final int position) {
        if(filteredInfoneContactsRVItems.get(position).isChecked){
            holder.checked.setVisibility(View.VISIBLE);

        }
        else{
            holder.checked.setVisibility(View.GONE);
        }
        holder.nametv.setText(filteredInfoneContactsRVItems.get(position).getName());
        if (filteredInfoneContactsRVItems.get(position).getDesc() != null) {
            holder.desctv.setText(filteredInfoneContactsRVItems.get(position).getDesc());
            holder.desctv.setVisibility(View.VISIBLE);

        }
        if (filteredInfoneContactsRVItems.get(position).getImageThumb() != null) {
            holder.userAvatar.setVisibility(View.VISIBLE);
            Uri imageuri = Uri.parse(filteredInfoneContactsRVItems.get(position).getImageThumb());
            holder.userAvatar.setImageURI(imageuri);
            holder.dialogVerifyProfileImg.setImageURI(imageuri);
            holder.dialogRequestCallProfileImg.setImageURI(imageuri);
        }
        final InfoneContactsRVItem currentContact = filteredInfoneContactsRVItems.get(position);
        holder.whatsAppImageBtn.setVisibility(View.GONE);
        holder.callImageBtn.setVisibility(View.GONE);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Herre",Integer.toString(holder.checked.getVisibility()));
                if(holder.checked.getVisibility()==View.GONE){
                    if(onItemCheckListener.onItemCheck(currentContact)){
                        holder.checked.setVisibility(View.VISIBLE);
                        filteredInfoneContactsRVItems.get(position).isChecked = true;
//                        Log.d("Herre",Integer.toString(holder.itemView.findViewById(R.id.item_checked).getVisibility()));

                    }

                }
                else{
                    if(onItemCheckListener.onItemUncheck(currentContact)){
                        holder.checked.setVisibility(View.GONE);
                        filteredInfoneContactsRVItems.get(position).isChecked = false;
//                        Log.d("Herre",Integer.toString(holder.itemView.findViewById(R.id.item_checked).getVisibility()));

                    }
                }
            }
        });

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredInfoneContactsRVItems = infoneContactsRVItems;
                } else {
                    List<InfoneContactsRVItem> filteredList = new ArrayList<>();
                    for (InfoneContactsRVItem row : infoneContactsRVItems) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filteredInfoneContactsRVItems = new ArrayList<>(filteredList);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredInfoneContactsRVItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredInfoneContactsRVItems = (ArrayList<InfoneContactsRVItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return filteredInfoneContactsRVItems.size();
    }
}
