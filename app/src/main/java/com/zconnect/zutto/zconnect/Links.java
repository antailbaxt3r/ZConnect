package com.zconnect.zutto.zconnect;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.LinksRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.ListItem;
import com.zconnect.zutto.zconnect.itemFormats.NewRequestItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.Product;
import com.zconnect.zutto.zconnect.utilities.RequestTypeUtilities;

import java.util.HashMap;
import java.util.Vector;

public class Links extends BaseActivity {

    ValueEventListener mValueEventListener ;
    private  boolean flagNoLinks;
    LinksRVAdapter  linksRVAdapter;
    DatabaseReference linkReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        final Vector<ListItem> LinksList=new Vector<ListItem>();


        linkReference= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("links");

        mValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinksList.clear();

                ListItem singleProduct=new ListItem();
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    try{
                        singleProduct = shot.getValue(ListItem.class);
                        if(singleProduct.getLinkURL()!=null&& singleProduct.getTitle()!=null) {
                            LinksList.add(singleProduct);

                        }
                    }
                    catch (Exception e){
                        Log.d("Error Alert", e.getMessage());
                    }
                }

                linksRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        RecyclerView links=(RecyclerView)findViewById(R.id.menu_links);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        links.setLayoutManager(linearLayoutManager);
        linksRVAdapter=new LinksRVAdapter(LinksList,Links.this);
        links.setAdapter(linksRVAdapter);
        findViewById(R.id.fab_add_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context;
                Dialog addLinkDialog = new Dialog(Links.this);
                addLinkDialog.setContentView(R.layout.dialog_add_link);
                addLinkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText linkTitle = addLinkDialog.findViewById(R.id.add_link_title);
                EditText linkText = addLinkDialog.findViewById(R.id.add_link_link);
                Button cancelButton = addLinkDialog.findViewById(R.id.add_link_cancel_btn);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addLinkDialog.dismiss();
                    }
                });
                Button requestButton = addLinkDialog.findViewById(R.id.add_link_request_btn);
                requestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(linkTitle.getText().toString().trim().equals( "" ) || linkText.getText().toString().trim().equals("")){
                            Toast.makeText(Links.this,"Fields are empty",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        NewRequestItemFormat requestItemFormat = new NewRequestItemFormat();
                        DatabaseReference requestForumTabs = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
                        DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        final DatabaseReference newPush=requestForumTabs.push();

                        final HashMap<String, Object> requestMap = new HashMap<>();
                        requestMap.put("Type", RequestTypeUtilities.TYPE_LINKS);
                        requestMap.put("key",newPush.getKey());
                        requestMap.put("Name",linkTitle.getText().toString());
                        requestMap.put("PostTimeMillis",System.currentTimeMillis());
                        requestMap.put("link",linkText.getText().toString());

                        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final HashMap<String,Object> postedBy = new HashMap<>();
                                postedBy.put("Username",dataSnapshot.child("username").getValue().toString());
                                postedBy.put("ImageThumb",dataSnapshot.child("imageURLThumbnail").getValue().toString());
                                postedBy.put("UID",dataSnapshot.child("userUID").getValue().toString());
                                if(dataSnapshot.child("userType").getValue(String.class).equals("admin")){
                                    final HashMap<String, Object> map = new HashMap<>();
                                    final DatabaseReference linksRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/links");
                                    map.put("linkTitle", linkTitle.getText().toString());
                                    map.put("linkURL", linkText.getText().toString());
                                    map.put("UID",dataSnapshot.getKey());
                                    linksRef.child(newPush.getKey()).setValue(map);
                                    Toast.makeText(Links.this,"Link posted successfully",Toast.LENGTH_LONG).show();
                                    addLinkDialog.dismiss();

                                }
                                else{
                                requestMap.put("PostedBy",postedBy);
                                newPush.setValue(requestMap);
                                    Toast.makeText(Links.this,"Link sent for Verification",Toast.LENGTH_LONG).show();
                                    addLinkDialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });
                addLinkDialog.show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        linkReference.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        linkReference.removeEventListener(mValueEventListener);
    }
}
