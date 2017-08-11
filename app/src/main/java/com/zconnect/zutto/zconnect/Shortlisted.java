package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.ArrayList;

public class Shortlisted extends BaseActivity {

    private ArrayList<String> names = new ArrayList<>(),nos = new ArrayList<>(),images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortlisted);

        setToolbar();
        setToolbarTitle("People who shortlisted");
        showBackButton();

        final String key = getIntent().getStringExtra("Key");

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        final adapter adapter = new adapter();

        LinearLayoutManager productLinearLayout = new LinearLayoutManager(this);
        productLinearLayout.setReverseLayout(true);
        productLinearLayout.setStackFromEnd(true);

        rv.setLayoutManager(productLinearLayout);
        rv.setAdapter(adapter);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("storeroom").child(key).child("UsersReserved");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showProgressDialog();
                nos.clear();
                names.clear();
                images.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String user = snapshot.getKey();
                    FirebaseDatabase.getInstance().getReference("Users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String email = dataSnapshot.child("Email").getValue(String.class);
                            FirebaseDatabase.getInstance().getReference("Phonebook").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() != 0) {
                                        PhonebookDisplayItem item = new PhonebookDisplayItem();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                            item = snapshot.getValue(PhonebookDisplayItem.class);

                                        if (item == null)
                                            return;
                                        try {
                                            names.add(item.getName());
                                            nos.add(item.getNumber());
                                            images.add(item.getImageurl());
                                        } catch (Exception e) {}
                                        adapter.notifyDataSetChanged();

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                hideProgressDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });

        showToast(names.size()+"");
    }
    class vh extends RecyclerView.ViewHolder {

        public vh(View itemView) {
            super(itemView);
        }

        private void setData(final int pos) {
            final SimpleDraweeView image = (SimpleDraweeView) itemView.findViewById(R.id.phonebook_item_format_image);
            image.setImageResource(R.drawable.defaultprofile);
            Picasso.with(itemView.getContext()).load(images.get(pos)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    image.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    image.setImageResource(R.drawable.defaultprofile);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });;

            ((TextView)itemView.findViewById(R.id.phonebook_name1)).setText(names.get(pos));
            ((TextView)itemView.findViewById(R.id.phonebook_number1)).setText(nos.get(pos));
            itemView.findViewById(R.id.callbutton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nos.get(pos))));
                }
            });

            showToast(names.get(pos)+"\t"+nos.get(pos));
        }

    }
    class adapter extends RecyclerView.Adapter<vh> {

        @Override
        public vh onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View person = inflater.inflate(R.layout.phonebook_item_format, parent, false);
            return new vh(person);
        }

        @Override
        public void onBindViewHolder(vh holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return names.size();
        }
    }
}
