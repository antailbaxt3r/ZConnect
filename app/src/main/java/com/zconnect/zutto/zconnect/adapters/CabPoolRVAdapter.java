package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.zconnect.zutto.zconnect.CabPoolListOfPeople;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

/**
 * Created by shubhamk on 26/7/17.
 */


public class CabPoolRVAdapter extends RecyclerView.Adapter<CabPoolRVAdapter.ViewHolder> {
    Context context;
    TreeMap<Double,CabItemFormat> treeMap_double;
    TreeMap<String,CabItemFormat> treeMap_string;
    String sourceText, destinationText, timeText, dateText, peopleText;
    DatabaseReference cabpoolReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs");
    Typeface regular, light, semiBold;
    ArrayList<CabItemFormat> array;
    String url = "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
    Vector<CabItemFormat> cabItemFormat;
    String peopletextfinal;
    private int i;

    public CabPoolRVAdapter(Context context, TreeMap<String,CabItemFormat> treeMap_string ,int a) {
        this.context = context;
        this.treeMap_string = treeMap_string;
        array=new ArrayList<>(treeMap_string.values());
        i=0;
        Log.e("RV","tree map");
    }


    public CabPoolRVAdapter(Context context, TreeMap<Double,CabItemFormat> treeMap_double) {
        this.context = context;
        this.treeMap_double = treeMap_double;
        array=new ArrayList<>(treeMap_double.values());

        i=0;
        Log.e("RV","tree map");
    }

    public CabPoolRVAdapter(Context context, Vector<CabItemFormat> cabItemFormat){
        this.context = context;
        this.cabItemFormat=cabItemFormat;
        i=1;
        Log.e("RV","cabitem");
    }

    @Override
    public CabPoolRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cabpool_item_format, parent, false);
        regular = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Regular.ttf");
        light = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        semiBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-SemiBold.ttf");

        return new CabPoolRVAdapter.ViewHolder(contactView);

    }

    @Override
    public void onBindViewHolder(final CabPoolRVAdapter.ViewHolder holder, int position) {





        if(i==0){
           DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
           DateTime date = null;
           try {
               DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
               date = dtf.parseDateTime(array.get(position).getDate());
           }catch (Exception e){}


//           holder.date.setText(date.toString("MMM") + " " + date.getDayOfMonth() + " " + date.getYearOfEra());
           holder.date.setText(date.toString("MMM") + " " + date.getDayOfMonth());
           holder.destination.setText(array.get(position).getDestination());
           holder.source.setText(array.get(position).getSource());
           String key0 = cabItemFormat.get(position).getKey();
           cabpoolReference.child(key0).child("usersListItemFormats").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   int size = (int) dataSnapshot.getChildrenCount();
                   peopleText = Integer.toString(size);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

           holder.people.setText(peopleText);
//       if(array.get(position).getFrom()!=0){ holder.time.setText(array.get(position).getFrom()+":00 to "+array.get(position).getTo()+":00");}
           String fromAmPm = array.get(position).getFrom()<12 ? "AM" : "PM";
           int fromTime = array.get(position).getFrom()<=12 ? array.get(position).getFrom() : array.get(position).getFrom() - 12;
           fromTime = fromTime == 0 ? 12 : fromTime;
           String toAmPm = array.get(position).getTo()<12 ? "AM" : "PM";
           int toTime = array.get(position).getTo()<=12 ? array.get(position).getTo() : array.get(position).getTo() - 12;
           toTime = toTime == 0 ? 12 : toTime;
           String timeText = fromTime + " " + fromAmPm + " - " + toTime + " " + toAmPm;
           holder.time.setText(timeText);

//           if(array.get(position).getFrom()!=0){ }
//           else{holder.time.setText(array.get(position).getTime());}
           Log.e("RV","array");
       }
        if(i==1){

            DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
            DateTime date = null;
            try {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                date = dtf.parseDateTime(cabItemFormat.get(position).getDate());
            }catch (Exception e){}
//            holder.date.setText(date.toString("MMM") + " " + date.getDayOfMonth() + " " + date.getYearOfEra());
            holder.date.setText(date.toString("MMM") + " " + date.getDayOfMonth());

            final String key1 = cabItemFormat.get(position).getKey();

            cabpoolReference.child(key1).child("usersListItemFormats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int size = (int) dataSnapshot.getChildrenCount();
                    //System.out.println("sizevalue for " + key1 + " : " + size); //for testing
                    peopleText = Integer.toString(size);
                    //System.out.println("peopletextvalue: " + peopleText); //for testing
                    //System.out.println("INSIDE DATASNAPSHOT"); //for testing
                    holder.people.setText(peopleText + " People");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //System.out.println("OUTSIDE DATASNAPSHOT"); //for testing
            //System.out.println("peopletext outside: " + peopleText); //for testing

            holder.destination.setText(cabItemFormat.get(position).getDestination());
            holder.source.setText(cabItemFormat.get(position).getSource());
//      if(cabItemFormat.get(position).getFrom()!=0)  {
//          holder.time.setText(cabItemFormat.get(position).getFrom()+":00 to "+cabItemFormat.get(position).getTo()+":00");
          String fromAmPm = cabItemFormat.get(position).getFrom()<12 ? "AM" : "PM";
          int fromTime = cabItemFormat.get(position).getFrom()<=12 ? cabItemFormat.get(position).getFrom() : cabItemFormat.get(position).getFrom() - 12;
          fromTime = fromTime ==  0 ? 12 : fromTime;
          String toAmPm = cabItemFormat.get(position).getTo()<12 ? "AM" : "PM";
          int toTime = cabItemFormat.get(position).getTo()<=12 ? cabItemFormat.get(position).getTo() : cabItemFormat.get(position).getTo() - 12;
          toTime = toTime == 0 ? 12 : toTime;
          String timeText = fromTime + " " + fromAmPm + " - " + toTime + " " + toAmPm;
          holder.time.setText(timeText);
//      } else{
//          holder.time.setText(cabItemFormat.get(position).getTime());
//       }
        }

    }

    @Override
    public int getItemCount() {
      if(i==0){
          Log.e("ABC1",String.valueOf(array.size()));
          return array.size();
       }else{
          return cabItemFormat.size();
      }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
       TextView source,destination,details,time,date, people;

        String key;
        LinearLayout list_people, share;
        public ViewHolder(View itemView) {
            super(itemView);

            source =(TextView)itemView.findViewById(R.id.source);
            people = itemView.findViewById(R.id.people_count);
            destination =(TextView)itemView.findViewById(R.id.destination);
            time=(TextView)itemView.findViewById(R.id.time_range);
            date=(TextView)itemView.findViewById(R.id.date);
            list_people = (LinearLayout) itemView.findViewById(R.id.list);
            share = (LinearLayout) itemView.findViewById(R.id.sharecab);
            list_people.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    meta.put("type","fromFeature");
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_OPEN_LIST_OF_PEOPLE);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    sourceText = source.getText().toString();
                    destinationText = destination.getText().toString();
                    timeText = time.getText().toString();
                    dateText = date.getText().toString();

                if(i==0) {
                    Intent intent = new Intent(context, CabPoolListOfPeople.class);
                    intent.putExtra("key", array.get(getAdapterPosition()).getKey());
                    intent.putExtra("date", (array.get(getAdapterPosition()).getDT()).substring(0,8));
                    intent.putExtra("sourceText", sourceText);
                    intent.putExtra("destinationText", destinationText);
                    intent.putExtra("timeText", timeText);
                    intent.putExtra("dateText", dateText);
                    intent.putExtra("peopleText", people.getText().toString());

                    context.startActivity(intent);
                }

                if(i==1) {
                    Intent intent = new Intent(context, CabPoolListOfPeople.class);
                    intent.putExtra("key", cabItemFormat.get(getAdapterPosition()).getKey());
                    intent.putExtra("date", (cabItemFormat.get(getAdapterPosition()).getDT()).substring(0,8));
                    intent.putExtra("sourceText", sourceText);
                    intent.putExtra("destinationText", destinationText);
                    intent.putExtra("timeText", timeText);
                    intent.putExtra("dateText", dateText);
                    intent.putExtra("peopleText", people.getText().toString());

                    context.startActivity(intent);
                }}
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();

                    try {
                        meta.put("source", String.valueOf(cabItemFormat.get(getAdapterPosition()).getSource()));
                        meta.put("destination", String.valueOf(cabItemFormat.get(getAdapterPosition()).getDestination()));
                    }catch (Exception e){}

                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_SHARE);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    Uri BASE_URI = Uri.parse("http://www.zconnect.com/cabpooling/");

                    Uri APP_URI = BASE_URI.buildUpon().appendQueryParameter("key", cabItemFormat.get(getAdapterPosition()).getKey())
                            .appendQueryParameter("communityRef", communityReference)
                            .build();
                    Log.d("AAAAAAA", String.valueOf(getAdapterPosition()));
                    String encodedUri = null;
                    try {
                        encodedUri = URLEncoder.encode(APP_URI.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                  if(i==0) {

                      Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                              .setLongLink(Uri.parse("https://zconnect.page.link/?link=" + encodedUri + "&apn=com.zconnect.zutto.zconnect&amv=11"))
                              .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                              .buildShortDynamicLink()
                              .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                  @Override
                                  public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                      if (task.isSuccessful()) {
                                          //short link
                                          final Uri shortLink = task.getResult().getShortLink();
                                          Uri flowcharLink = task.getResult().getPreviewLink();
                                          Intent intent = new Intent();
                                          intent.setAction(Intent.ACTION_SEND);
                                          intent.putExtra(Intent.EXTRA_TEXT, "Join my cabpool from " + array.get(getAdapterPosition()).getSource() +
                                                  " to " + array.get(getAdapterPosition()).getDestination() + " on " +
                                                  array.get(getAdapterPosition()).getDate() +
                                                  "\n Use ZConnect app to join the pool \n"
                                                  + shortLink);

                                          intent.setType("text/plain");
                                          intent.setPackage("com.whatsapp");
                                          context.startActivity(intent);

                                      }
                                      else {
                                          Log.d("CabPoolRVAdapter", task.getException().getMessage());
                                      }
                                  }
                              });
                  }
                    if(i==1) {

                        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                                .setLongLink(Uri.parse("https://zconnect.page.link/?link=" + encodedUri + "&apn=com.zconnect.zutto.zconnect&amv=11"))
                                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                                .buildShortDynamicLink()
                                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                    @Override
                                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                        if (task.isSuccessful()) {
                                            //short link
                                            final Uri shortLink = task.getResult().getShortLink();
                                            Uri flowcharLink = task.getResult().getPreviewLink();
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_TEXT, "Join my cabpool from " + cabItemFormat.get(getAdapterPosition()).getSource() +
                                                    " to " + cabItemFormat.get(getAdapterPosition()).getDestination() + " on " +
                                                    cabItemFormat.get(getAdapterPosition()).getDate() +
                                                    "\n Use the ZConnect app to join the pool \n"
                                                    + shortLink);
                                            intent.setType("text/plain");
                                            intent.setPackage("com.whatsapp");
                                            context.startActivity(intent);
                                        }
                                        else {
                                            Log.d("CabPoolRVAdapter", task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                }
            });
//            source.setTypeface(semiBold);
//            destination.setTypeface(semiBold);
//            details.setTypeface(customFont2);
//            time.setTypeface(regular);
//            date.setTypeface(regular);
//            list_people.setTypeface(semiBold);
//            share.setTypeface(semiBold);

//            TextView source_head = (TextView)itemView.findViewById(R.id.source_head);
//            TextView destination_head = (TextView)itemView.findViewById(R.id.destination_head);
//            TextView date_head = (TextView)itemView.findViewById(R.id.date_head);
//            TextView time_head = (TextView)itemView.findViewById(R.id.time_head);

//            source_head.setTypeface(customFont);
//            destination_head.setTypeface(customFont);
//            date_head.setTypeface(customFont);
//            time_head.setTypeface(customFont);

        }
    }
}
