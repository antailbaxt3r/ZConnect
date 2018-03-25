package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.Infone2CategoryModel;
import com.zconnect.zutto.zconnect.adapters.Infone2RVAdapter;

import java.util.ArrayList;

public class Infone2Activity extends AppCompatActivity {

    RecyclerView recyclerViewCat;
    FloatingActionButton fabCatAdd;
    ArrayList<Infone2CategoryModel> categoriesList = new ArrayList();
    DatabaseReference databaseReferenceCat;
    ValueEventListener listener;
    Infone2RVAdapter infone2RVAdapter;
    private static final int REQUEST_PHONE_CALL = 1;
    private SharedPreferences communitySP;
    public String communityReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone2);

        recyclerViewCat = (RecyclerView) findViewById(R.id.rv_cat_infone);

        fabCatAdd = (FloatingActionButton) findViewById(R.id.fab_cat_infone);


        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceCat = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categoriesInfo");

        recyclerViewCat.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                categoriesList = new ArrayList<>();
                for (DataSnapshot childSnapShot :
                        dataSnapshot.getChildren()) {
                    String name = childSnapShot.child("name").getValue(String.class);
                    String imageurl = childSnapShot.child("imageurl").getValue(String.class);
                    String admin = childSnapShot.child("admin").getValue(String.class);
                    String catId = childSnapShot.getKey();
                    Infone2CategoryModel infone2CategoryModel = new Infone2CategoryModel(name, imageurl, admin, catId);
                    categoriesList.add(infone2CategoryModel);

                }

                infone2RVAdapter = new Infone2RVAdapter(categoriesList, Infone2Activity.this);
                recyclerViewCat.setAdapter(infone2RVAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(Infone2Activity.class.getName(), "database error" + databaseError.toString());
            }
        };
        databaseReferenceCat.addValueEventListener(listener);

        fabCatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("tt", "data fab");

                SharedPreferences sharedPref = Infone2Activity.this.getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {
                    Intent addCatIntent = new Intent(Infone2Activity.this,InfoneAddCatActivity.class);
                    //addCatIntent("categoryId",);
                    startActivity(addCatIntent);
                    //addDialog();
                } else {
                    Toast.makeText(Infone2Activity.this, "Log in to use this function", Toast.LENGTH_SHORT).show();
                }
            }
        });


        askCallPermissions();

    }

    private void addDialog() {

        Log.e("tt", "data fab");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Add new infone Category");
        //alertDialog.setMessage("Category name");

        final EditText newCategoryET = new EditText(this);
        newCategoryET.setInputType(InputType.TYPE_CLASS_TEXT);
        newCategoryET.setHint("Category name");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        newCategoryET.setLayoutParams(lp);
        alertDialog.setView(newCategoryET);
        alertDialog.setIcon(R.drawable.ic_add_white_36dp);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        final String newCat = newCategoryET.getText().toString();

                        if (!newCat.isEmpty()) {
                            Toast.makeText(Infone2Activity.this, "Add a contact in your new category",
                                    Toast.LENGTH_SHORT).show();
                            Intent addContactIntent = new Intent(Infone2Activity.this,
                                    InfoneAddContactActivity.class);
                            addContactIntent.putExtra("categoryName", newCat);
                            startActivity(addContactIntent);
                        }
                    }
                });

        alertDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceCat.removeEventListener(listener);
    }

    private void askCallPermissions() {

        if (ContextCompat.checkSelfPermission(Infone2Activity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Infone2Activity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startActivity(intent);
                } else {
                    askCallPermissions();
                }
                return;
            }
        }
    }
}
