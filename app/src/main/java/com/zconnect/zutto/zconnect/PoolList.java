package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import java.util.Vector;

import static android.icu.util.HebrewCalendar.AV;

public class PoolList extends AppCompatActivity {
    RecyclerView poolrv;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");
    DatabaseReference pool;
    Query query;
    String Date;//present date
    TextView defaultmsg;
    String source,destination,date,formatted_date,time_to,time_from;
    String reference;
    String reference_default="Cab";
    String reference_Old="archive/Cab";
    Vector<CabItemFormat> cabItemFormatVector = new Vector<>();
    CabPoolRVAdapter adapter;
    ValueEventListener newListener;
    String name, number, email;
    ProgressBar progressBar;
    TreeMap<Double,CabItemFormat> treeMap_double=new TreeMap<>();
    TreeMap<String,CabItemFormat> treeMap_string=new TreeMap<>();

    Context mcontext;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cab");
    private FirebaseAuth mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PoolList.this, AddCabPool.class);
                startActivity(intent);
            }
        });
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        //getting present dates and defining format for input and output date
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat input = new SimpleDateFormat("dd/M/yyyy");
        SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
        Date = output.format(c.getTime());

        //getting values from intent
        try {
            source = getIntent().getStringExtra("source");
            destination = getIntent().getStringExtra("destination");
            date = getIntent().getStringExtra("date");
            time_to = getIntent().getStringExtra("time_to");
            time_from = getIntent().getStringExtra("time_from");

        } catch (Exception e) {

        }
   //     Log.e("ABC",source);
//        Log.e("ABC",destination);



            try {
                Date abc=input.parse(date);
                formatted_date=output.format(abc);

            }catch (Exception e){}

        //Setting old database or new database
        if(date==null){

        reference=reference_default;

            }else{
                if(Date.compareTo(formatted_date)>0){
                reference=reference_Old;
                }else{
                reference=reference_default;
           }
        }

        pool = FirebaseDatabase.getInstance().getReference().child(reference);
        query=pool.orderByChild("DT");

        mUser = FirebaseAuth.getInstance();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (phonebookDisplayItem == null)
                        return;
                    if (email != null) {
                        if (phonebookDisplayItem.getEmail() != null) {
                            if (phonebookDisplayItem.getEmail().equals(email)) {
                                name = phonebookDisplayItem.getName();
                                number = phonebookDisplayItem.getNumber();

                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        poolrv = (RecyclerView) findViewById(R.id.poolrv);
        defaultmsg = (TextView) findViewById(R.id.pool_errorMessage1);
        progressBar = (ProgressBar) findViewById(R.id.content_pool_progress);
        defaultmsg.setVisibility(View.INVISIBLE);
        query.keepSynced(true);
         mcontext=this;



        newListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                defaultmsg.setVisibility(View.INVISIBLE);
                poolrv.setVisibility(View.VISIBLE);
                cabItemFormatVector.clear();
                treeMap_double.clear();
                treeMap_string.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    CabItemFormat cabItemFormat = shot.getValue(CabItemFormat.class);

                    cabItemFormatVector.add(cabItemFormat);
                }

                Double Av_asked;
                Boolean hasSource=check(source);
                Boolean hasDestination=check(destination);
                Boolean hasDate=check(date);
                Boolean hasTime_from=check(time_from);
                if(hasTime_from){
                    double time1 = Double.valueOf(time_to.substring(0, 2));
                    double time2 = Double.valueOf(time_from.substring(0, 2));
                    Av_asked=(time1+time2)/2;
                }else{
                    Av_asked=0.0;
                }
                Log.e("ABC",String.valueOf(cabItemFormatVector.size()));

                for(int i=0;i<cabItemFormatVector.size();i++) {
                  //  Log.e("ABC",String.valueOf(cabItemFormatVector.get(i).getSource()));

                    if (hasSource) {
                        if (equalSource(i, source)) {
                        } else {
                            continue;
                        }
                       // Log.e("ABC1",destination);
                    }
                    if (hasDestination) {
                        if (equalDestination(i, destination)) {
                        } else {
                            continue;
                        }
                    }
                    Log.e("ABC12","WTF");

                    if (hasDate) {
                        if (equalDate(i, formatted_date)) {
                        } else {
                            continue;
                        }
                    }

                    Log.e("ABC1","NOT THIS");

                    if(hasTime_from){treeMap_double.put(equalTime(i,Av_asked),cabItemFormatVector.get(i));}
                 else{treeMap_string.put(cabItemFormatVector.get(i).getDT(),cabItemFormatVector.get(i));}
                    Log.e("ABC1","MOTHER FUCKER");

                }


                if (treeMap_string.isEmpty()&&treeMap_double.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    poolrv.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PoolList.this);
                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("No CabPools found . \n Would you like to add one ?")
                            .setTitle("Alert");      

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (isNetworkAvailable(getApplicationContext())) {
                                if (name != null && number != null) {

                                     Intent intent=new Intent(PoolList.this,AddCabPool.class);
                                    startActivity(intent);

                                }else{
                                Snackbar snack = Snackbar.make(fab, "Please add your contact to Infone before adding a pool.", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                snack.show();
                            }} else {
                                Snackbar snack = Snackbar.make(fab, "No internet. Please try again later.", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                snack.show();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                    //   Toast.makeText(getApplicationContext(), String.valueOf(cabItemFormats.size()), Toast.LENGTH_SHORT).show();
                    poolrv.setHasFixedSize(true);
                    poolrv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
                   if(hasTime_from) {
                       adapter = new CabPoolRVAdapter(mcontext, treeMap_double);
                   }else{
                       adapter = new CabPoolRVAdapter(mcontext, treeMap_string,1);

                   }
                   poolrv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mUser.getCurrentUser() != null)
            email = mUser.getCurrentUser().getEmail();


    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    @Override
    protected void onResume() {
        super.onResume();

        query.addValueEventListener(newListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        query.removeEventListener(newListener);
    }


    public boolean check(String abc){
        if(abc!=null){
            return true;
        }else{
            return false;
        }
    }

    public boolean equalSource(int i,String source){
        if(cabItemFormatVector.get(i).getSource().equals(source)){
            return true;
        }else {
                return false;
        }
    }


    public boolean equalDestination(int i,String destination){
        if(cabItemFormatVector.get(i).getDestination().equals(destination)){
            return true;
        }else {
            return false;
        }
    }

    public boolean equalDate(int i,String dateyyyyMdd){
        String pooldate=cabItemFormatVector.get(i).getDT().substring(0,8);
        Log.e("ABC_d",pooldate);

        if(pooldate.equals(dateyyyyMdd)){
            return true;
        }else {
            return false;
        }
    }

    public double equalTime(int i,double AV_asked){
        double poolAv=Double.valueOf(cabItemFormatVector.get(i).getDT().substring(9,11));

         if(poolAv>AV_asked){
             return poolAv-AV_asked;
         }else{
             return AV_asked-poolAv;
         }

        }
    }

