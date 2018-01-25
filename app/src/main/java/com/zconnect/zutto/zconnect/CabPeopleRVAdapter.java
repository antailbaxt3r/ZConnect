package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.PostEmails.email;

/**
 * Created by shubhamk on 27/7/17.
 */

public class CabPeopleRVAdapter extends RecyclerView.Adapter<CabPeopleRVAdapter.ViewHolder> {
    Context context;
    Vector<CabListItemFormat> cabListItemFormats;


    private SharedPreferences communitySP;
    public String communityReference;

    public CabPeopleRVAdapter(Context context, Vector<CabListItemFormat> cabListItemFormats) {
        this.context = context;
        this.cabListItemFormats = cabListItemFormats;

    }

    @Override
    public CabPeopleRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cab_people_item_format, parent, false);
        return new CabPeopleRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(CabPeopleRVAdapter.ViewHolder holder, int position) {
        holder.name.setText(cabListItemFormats.get(position).getName());
        holder.number.setText(cabListItemFormats.get(position).getPhonenumber());
    }

    @Override
    public int getItemCount() {
        return cabListItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        ImageView call;
        View rv_item;
        Intent intent;



        public ViewHolder(View itemView) {
            super(itemView);

            communitySP = context.getSharedPreferences("communityName", MODE_PRIVATE);
            communityReference = communitySP.getString("communityReference", null);

            call = (ImageView) itemView.findViewById(R.id.ib_call_contact_item);
            name = (TextView) itemView.findViewById(R.id.cab_name);
            number = (TextView) itemView.findViewById(R.id.cab_number);
            rv_item=itemView.findViewById(R.id.rv_item);
            Typeface customFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            name.setTypeface(customFont);

            Typeface customFont2 = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            number.setTypeface(customFont2);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + Long.parseLong(number.getText().toString().trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            rv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                       intent=new Intent(context, PhonebookDetails.class);
                    String  name = cabListItemFormats.get(getAdapterPosition()).getName();
                    String number = cabListItemFormats.get(getAdapterPosition()).getPhonenumber();

                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook").child(number);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                PhonebookDisplayItem phonebookDisplayItem = dataSnapshot.getValue(PhonebookDisplayItem.class);
                            Log.e("ABC",phonebookDisplayItem.getName());
                                   intent.putExtra("name",phonebookDisplayItem.getName());
                                    intent.putExtra("desc",phonebookDisplayItem.getDesc());
                                   // intent.putExtra("contactDescTv",phonebookDisplayItem.get());
                                    intent.putExtra("image",phonebookDisplayItem.getImageurl());
                                    intent.putExtra("email",phonebookDisplayItem.getEmail());
                                    intent.putExtra("skills",phonebookDisplayItem.getSkills());
                                    intent.putExtra("category",phonebookDisplayItem.getCategory());
                                    intent.putExtra("Uid",phonebookDisplayItem.getUid());
                                    context.startActivity(intent);

                                }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }
}
