package com.zconnect.zutto.zconnect.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.BuildConfig;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.InfoneActivity;
import com.zconnect.zutto.zconnect.addActivities.AddEvent;
import com.zconnect.zutto.zconnect.addActivities.AddNotices;
import com.zconnect.zutto.zconnect.addActivities.AddProduct;
import com.zconnect.zutto.zconnect.CabPooling;
import com.zconnect.zutto.zconnect.addActivities.AddStatus;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.addActivities.CreatePoll;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;

import java.util.HashMap;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class HomeBottomSheet extends BottomSheetDialogFragment{

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private Button test;
    private DatabaseReference communityFeaturesRef;
    private HomeActivity mHomeActivity;

    public HomeBottomSheet(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View bottomSheetView = inflater.inflate(R.layout.content_home_bottomsheet, null);
        final LinearLayout bottomSheetAddEvent = (LinearLayout) bottomSheetView.findViewById(R.id.addEvent_bottomSheet);
        final LinearLayout bottomSheetAddProduct = (LinearLayout) bottomSheetView.findViewById(R.id.addProduct_bottomSheet);
        LinearLayout bottomSheetAddMessage = (LinearLayout) bottomSheetView.findViewById(R.id.addMessage_bottomSheet);
        LinearLayout bottomSheetCreatePoll = (LinearLayout) bottomSheetView.findViewById(R.id.createPoll_bottomSheet);
        final LinearLayout bottomSheetSearchPool = (LinearLayout) bottomSheetView.findViewById(R.id.searchPool_bottomSheet);
        final LinearLayout bottomSheetAddNotices = bottomSheetView.findViewById(R.id.add_notices_bottomSheet);
        LinearLayout bottomSheetAddContact = (LinearLayout) bottomSheetView.findViewById(R.id.addContact_bottomSheet);

        communityFeaturesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("communityFeatures");

        communityFeaturesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CommunityFeatures communityFeatures = dataSnapshot.getValue(CommunityFeatures.class);

                try {

                    if (communityFeatures.getCabpool().equals("true")){
                        bottomSheetSearchPool.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetSearchPool.setVisibility(View.GONE);
                    }


                    if (communityFeatures.getEvents().equals("true")){
                        bottomSheetAddEvent.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetAddEvent.setVisibility(View.GONE);
                    }


                    if (communityFeatures.getNotices().equals("true")){
                        bottomSheetAddNotices.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetAddNotices.setVisibility(View.GONE);
                    }


                    if (communityFeatures.getLinks().equals("true")){

                    }else {

                    }

                    if (communityFeatures.getStoreroom().equals("true")){
                        bottomSheetAddProduct.setVisibility(View.VISIBLE);
                    }else {
                        bottomSheetAddProduct.setVisibility(View.GONE);
                    }


                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View.OnClickListener addEventListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                Intent intent;
                intent = new Intent(view.getContext(), AddEvent.class);
                startActivity(intent);

            }
        };
        View.OnClickListener addProductListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                        .child("storeroom").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                    Intent intent = new Intent(view.getContext(), UpdateAppActivity.class);
                                    intent.putExtra("feature", "shops");
                                   view.getContext().startActivity(intent);

                                } else {

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta = new HashMap<>();

                                    meta.put("type", "fromRecents");

                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_PRODUCT_ADD_OPEN);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);

                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();
                                    //TODO app crashes on line 163/171
                                    //TODO UPDATE: FIXED
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mHomeActivity);
                                    Dialog addAskDialog = new Dialog(view.getContext());
                                    addAskDialog.setContentView(R.layout.new_dialog_box);
                                    addAskDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    addAskDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_outline_store_24px));
                                    TextView heading =  addAskDialog.findViewById(R.id.dialog_box_heading);
                                    heading.setText("Sell/Ask");
                                    TextView body = addAskDialog.findViewById(R.id.dialog_box_body);
                                    body.setText("Do you want to sell a product or ask for a product?");
                                    Button addButton = addAskDialog.findViewById(R.id.dialog_box_positive_button);
                                    addButton.setText("Sell");
                                    addButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(view.getContext(), AddProduct.class);
                                            intent.putExtra("type", ProductUtilities.TYPE_ADD_STR);
                                            view.getContext().startActivity(intent);
                                        }
                                    });
                                    Button askButton = addAskDialog.findViewById(R.id.dialog_box_negative_button);
                                    askButton.setText("Ask");
                                    askButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(view.getContext(), AddProduct.class);
                                            intent.putExtra("type", ProductUtilities.TYPE_ASK_STR);
                                            view.getContext().startActivity(intent);
                                        }
                                    });

                                    addAskDialog.show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                                 });

        }
        };

        View.OnClickListener addMessageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                meta.put("type","fromRecents");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_ADD_STATUS);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent intent;
                intent = new Intent(view.getContext(), AddStatus.class);
                startActivity(intent);
            }
        };

        View.OnClickListener createPollListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                Intent intent;
                intent = new Intent(view.getContext(), CreatePoll.class);
                startActivity(intent);
            }
        };

        View.OnClickListener searchPoolListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                        .child("cabpool").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                    Intent intent = new Intent(view.getContext(), UpdateAppActivity.class);
                                    intent.putExtra("feature", "shops");
                                    view.getContext().startActivity(intent);

                                } else {

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta = new HashMap<>();

                                    meta.put("type", "fromRecents");


                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_SEARCH_POOL_OPEN);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);

                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();
                                    Intent intent;
                                    intent = new Intent(view.getContext(), CabPooling.class);
                                    view.getContext().startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
        }};

        View.OnClickListener noticesListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                meta.put("type","fromRecents");
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_ADD_NOTICES);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent intent;
                intent = new Intent(v.getContext(), AddNotices.class);
                v.getContext()
                        .startActivity(intent);
            }
        };

        View.OnClickListener addContactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HomeBottomSheet.this.dismiss();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

//                Intent intent;
//                intent = new Intent(getContext(), InfoneActivity.class);
//                startActivity(intent);
                mHomeActivity.setActionBarTitle("Infone");
                mHomeActivity.tabs.getTabAt(3).select();
                getFragmentManager().beginTransaction().show(mHomeActivity.infone).commit();
                Toast.makeText(v.getContext(), "Choose a category to add a contact", Toast.LENGTH_LONG).show();
            }
        };

        bottomSheetAddEvent.setOnClickListener(addEventListener);
        bottomSheetAddProduct.setOnClickListener(addProductListener);
        bottomSheetAddMessage.setOnClickListener(addMessageListener);
        bottomSheetCreatePoll.setOnClickListener(createPollListener);
        bottomSheetSearchPool.setOnClickListener(searchPoolListener);
        bottomSheetAddNotices.setOnClickListener(noticesListener);
        bottomSheetAddContact.setOnClickListener(addContactListener);

        return bottomSheetView;
    }

    @Override
    public void onAttach(Activity activity) {
        mHomeActivity = (HomeActivity) activity;
        super.onAttach(activity);
    }
}
