package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class logoFlash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logo_flash);
        // Setting full screen view
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(checkPermission())
        {
             new Timer().schedule(new TimerTask(){
                        public void run() {
                            Intent intent = new Intent(logoFlash.this, home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }, 2800);
        }
        // Time Delay for the logo activity


//        String mediacl[] = new String[1];
//        {
//            String allMedical = "Wild Orchid's Spa and Salon";
//
//            Scanner scanner =new Scanner(allMedical);
//            for (int i=0;i<1;i++)
//            {
//                mediacl[i]=scanner.nextLine().trim();
//            }
//        }
//
//        String descMedical[] = new String[1];
//        {
//            String allDescMedical="Opp. Goa Ship Yard, Vasco da Gama, Goa 403802";
//            Scanner scanner =new Scanner(allDescMedical);
//            for (int i=0;i<1;i++)
//            {
//                descMedical[i]=scanner.nextLine().trim();
//            }
//        }
//
//        String imageMedical[] = new String[1];
//        {
//            String allImageMedical = "https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/Shops%2Fwild%20orchid.jpg?alt=media&token=1807f363-a098-40ef-b42e-653e893cfc88";
//            Scanner scanner = new Scanner(allImageMedical);
//            for(int i = 0; i<1; i++)
//            {
//                imageMedical[i] = scanner.nextLine().trim();
//            }
//        }
//

//
//        String email[] = new String[28];
//        {
//            String allEmail="";
//            Scanner scanner =new Scanner(allEmail);
//            for (int i=0;i<28;i++)
//            {
//                email[i]=scanner.nextLine().trim();
//            }
//        }

//        String desc[] = new String[9];
//        {
//            String allDesc="";
//            Scanner scanner =new Scanner(allDesc);
//            for (int i=0;i<9;i++)
//            {
//                desc[i]=scanner.nextLine().trim();
//            }
//        }

//        String numMedical[] = new String[1];
//        {
//            String allNumMedical="9765635238";
//            Scanner scanner =new Scanner(allNumMedical);
//            for (int i=0;i<1;i++)
//            {
//                numMedical[i]=scanner.nextLine().trim();
//            }
//        }
//
//        String latMedical[] = new String[1];
//        {
//            String allLatMedical = "15.399463";
//            Scanner scanner = new Scanner(allLatMedical);
//            for(int i = 0; i<1; i++)
//            {
//                latMedical[i]=scanner.nextLine().trim();
//            }
//        }
//
//        String lonMedical[] = new String[1];
//        {
//            String allLonMedical = "73.825037";
//            Scanner scanner = new Scanner(allLonMedical);
//            for(int i = 0; i<1; i++)
//            {
//                lonMedical[i]=scanner.nextLine().trim();
//            }
//        }


//        for (int i=0;i<10;i++)
//        {
//            Log.d("seperate","-------------------------------------------------------------------------------------------------");
//            Log.d("name",name[i]);
//            Log.d("number",num[i]);
////            Log.d("email",email[i]);
//            Log.d("desc",desc[i]);
//            Log.d("seperate","-------------------------------------------------------------------------------------------------");
//        }
//
//        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("Shop").child("Salon");
//        for (int i=0;i<1;i++)
//        {
//            DatabaseReference newData = mData.child(String.valueOf(i));
//            newData.child("details").setValue("Opens at 9 AM");
//            newData.child("imageurl").setValue(imageMedical[i]);
//            newData.child("lat").setValue(latMedical[i]);
//            newData.child("lon").setValue(lonMedical[i]);
//            newData.child("menuurl").setValue("https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/Shops%2F6-salon-barber-logo-design.png?alt=media&token=b5d5ba8e-b8f3-470a-a9d6-2d4e1ca274d7");
//            newData.child("name").setValue(mediacl[i]);
//            newData.child("number").setValue(numMedical[i]);
//        }

//
//        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("Phonebook");
//        for (int i=0;i<28;i++)
//        {
//            DatabaseReference newData = mData.child(num[i]);
//            newData.child("name").removeValue();
//            newData.child("category").removeValue();
//            newData.child("number").removeValue();
//            newData.child("email").removeValue();
//            newData.child("hostel").removeValue();
//            newData.child("desc").removeValue();
//            newData.child("imageurl").removeValue();
//        }

    }
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(logoFlash.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(logoFlash.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(logoFlash.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Permission to read storage is required .");
                    alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(logoFlash.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},7);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(logoFlash.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 7);
                }
                return false;
            } else {
                

                return true;
            }
        }
        else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 7:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new Timer().schedule(new TimerTask(){
                        public void run() {
                            Intent intent = new Intent(logoFlash.this, home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);

                } else {
                    Toast.makeText(this,"Permission Denied !, Retrying.",Toast.LENGTH_SHORT).show();
                    checkPermission();
                }
                break;
        }
    }
}
