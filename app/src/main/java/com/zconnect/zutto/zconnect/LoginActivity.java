package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CommunitiesItemFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDbRef;
    private DatabaseReference CommunitiesDatabaseReference;
    private ProgressDialog mProgress;
    @BindView(R.id.btn_google_sign_in)
    Button mGoogleSignInBtn;
    @BindView(R.id.btn_guest_login)
    Button mGuestLogInBtn;
    @BindView(R.id.bitsgoaemailinfo)
    TextView bpgcEmailInfo;
    private int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;
    String communityCode;
    private String userEmail;
    private Vector<CommunitiesItemFormat> CommunitiesEmails = new Vector<>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGuestLogInBtn.setOnClickListener(this);
        mGuestLogInBtn.setVisibility(View.GONE);
        mGoogleSignInBtn.setOnClickListener(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Typeface ralewayLight = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        Typeface ralewayMedium = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        bpgcEmailInfo.setTypeface(ralewayLight);
        mGoogleSignInBtn.setTypeface(ralewayMedium);
        mGuestLogInBtn.setTypeface(ralewayMedium);

    }

    private void launchGoogleSignInIntent() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.e(TAG, "launchGoogleSignInIntent: ");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: resCode = " + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_CANCELED) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("Signing in...");
            mProgress.show();
            if (result.isSuccess()) {
                Log.e(TAG, "onActivityResult: success");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "onActivityResult: not success" + result.getStatus().getStatusMessage());
                mProgress.dismiss();
                Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "Login failed", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snackbar.show();
            }
        } else {
            Log.d(TAG, "onActivityResult: " + resultCode);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "Authentication failed.", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                            snackbar.show();
                            mProgress.dismiss();
                        } else {
                            mUser = mAuth.getCurrentUser();
                            if (mUser != null) {
                                String userCommunity;
                                userEmail = mUser.getEmail();
                                userCommunity = userEmail.substring(userEmail.lastIndexOf('@'));

                                setCommunity(userCommunity);
                            }else {
                                logout();
                                mProgress.dismiss();
                            }
                        }
                    }
                });
    }

    @SuppressLint("ApplySharedPref")
    public void setGuestLoginPref(Boolean mode) {
        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        SharedPreferences.Editor editInfo = sharedPref.edit();
        editInfo.putBoolean("mode", mode);
        editInfo.commit();
    }

    public void setCommunity(String communityName){

        Toast.makeText(this, "Set Community", Toast.LENGTH_SHORT).show();
        communityCode = null;
        for (int i=0;i<CommunitiesEmails.size();i++)
        {
            if(CommunitiesEmails.get(i).getEmail().equals(communityName))
            {
                Toast.makeText(this, communityName, Toast.LENGTH_SHORT).show();
                communityCode = CommunitiesEmails.get(i).getCode();
                Toast.makeText(this, communityCode, Toast.LENGTH_SHORT).show();
            }
        }

        if(communityCode!=null)
        {
            SharedPreferences sharedPref2 = getSharedPreferences("communityName", MODE_PRIVATE);
            SharedPreferences.Editor editInfo2 = sharedPref2.edit();
            editInfo2.putString("communityReference", communityCode);
            editInfo2.commit();
            Intent i = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }else {
            logout();
            mProgress.dismiss();
            Toast.makeText(LoginActivity.this, "Login through your College Email", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        CommunitiesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");

        CommunitiesDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CommunitiesEmails.clear();
                for(DataSnapshot shot : dataSnapshot.getChildren()) {
                    try {
                        CommunitiesEmails.add(shot.getValue(CommunitiesItemFormat.class));
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        setGuestLoginPref(false);
    }

    private void logout() {
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: error message is " + connectionResult.getErrorMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google_sign_in: {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "No Internet. Can't Sign In.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                    snackbar.show();
                } else {
                    launchGoogleSignInIntent();
                }
                break;
            }
            case R.id.btn_guest_login: {
                Intent i = new Intent(LoginActivity.this,CommunitiesAround.class);
                startActivity(i);
                setGuestLoginPref(false);// need to work on this
                finish(); /*Make Sure HomeActivity exists*/
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mProgress != null) mProgress.dismiss(); //fix window leak
        super.onDestroy();
    }
}
