package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.CommunitiesItemFormat;

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
//    @BindView(R.id.btn_google_sign_in)
//    Button mGoogleSignInBtn;
    @BindView(R.id.layout_google_sign_in)
    LinearLayout mGoogleSignInLayout;
    @BindView(R.id.text_google_sign_in)
    TextView mGoogleSignInText;
    private int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;
    String communityCode;
    private String userEmail;
    @BindView(R.id.check_accept)
    CheckBox termsCheck;
    @BindView(R.id.terms_of_use)
    TextView termsOfUseText;
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

        termsOfUseText.setOnClickListener(this);
//        mGoogleSignInBtn.setOnClickListener(this);
        mGoogleSignInLayout.setOnClickListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Typeface ralewayLight = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        Typeface ralewayBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf");
//        mGoogleSignInBtn.setTypeface(ralewayMedium);
        mGoogleSignInText.setTypeface(ralewayBold);
//        mGuestLogInBtn.setTypeface(ralewayBold);

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
//                Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "Login failed", Snackbar.LENGTH_LONG);
                Snackbar snackbar = Snackbar.make(mGoogleSignInLayout, "Login failed", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();
            }
        } else {
            Log.d(TAG, "onActivityResult: " + resultCode);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        if(getIntent().getBooleanExtra("isReferred", false))
        {
            Log.d("RRRRRR", "IS REFERRED");
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth.getInstance().getCurrentUser()
                    .linkWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "linkWithCredential:success");
                                // Complete any post sign-up tasks here.
                                mUser = task.getResult().getUser();
                                if (mUser != null) {
                                    String userCommunity;
                                    userEmail = mUser.getEmail();
                                    userCommunity = userEmail.substring(userEmail.lastIndexOf('@'));
                                    Log.d("RRRRR mUser", mUser.toString());
                                    Log.d("RRRRR email", userEmail);
                                    Log.d("RRRRR comm", userCommunity);
                                    setCommunity(userCommunity);
                                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.referredAnonymousUser), Context.MODE_PRIVATE);
                                    sharedPreferences.edit().remove("referredUserID").apply();
                                    sharedPreferences.edit().remove("referredBy").apply();
                                }else {
                                    logout();
                                    mProgress.dismiss();
                                }
                            } else {
                                Log.w(TAG, "linkWithCredential:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Log.d("RRRRRR", "DID SOMETHING HAPPEN?");

        }

        else {
            Log.d("RRRRRR", "IS NOT REFERRED");
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
//                            Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "Authentication failed.", Snackbar.LENGTH_LONG);
                                Snackbar snackbar = Snackbar.make(mGoogleSignInLayout, "Authentication failed.", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
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
    }

    public void setCommunity(String communityName){


        communityCode = null;
        for (int i=0;i<CommunitiesEmails.size();i++)
        {
            if(CommunitiesEmails.get(i).getEmail().equals(communityName))
            {
                communityCode = CommunitiesEmails.get(i).getCode();

            }
        }

        if(communityCode!=null)
        {
            SharedPreferences sharedPref2 = getSharedPreferences("communityName", MODE_PRIVATE);
            SharedPreferences.Editor editInfo2 = sharedPref2.edit();
            editInfo2.putString("communityReference", communityCode);

            editInfo2.commit();

            SharedPreferences sharedPrefVerification = getSharedPreferences("userType", MODE_PRIVATE);
            SharedPreferences.Editor editInfo3 = sharedPrefVerification.edit();
            editInfo3.putBoolean("userVerification", true);

            editInfo3.commit();
            FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Intent i = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }else {
            mProgress.dismiss();

            SharedPreferences sharedPrefVerification = getSharedPreferences("userType", MODE_PRIVATE);
            SharedPreferences.Editor editInfo2 = sharedPrefVerification.edit();
            editInfo2.putBoolean("userVerification", false);
            editInfo2.commit();
            Intent i = new Intent(this,CommunitiesAround.class);

            startActivity(i);
            finish();
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
                        if (shot.hasChild("location")) {
                            CommunitiesEmails.add(shot.getValue(CommunitiesItemFormat.class));
                        }
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            case R.id.layout_google_sign_in: {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Snackbar snackbar = Snackbar.make(mGoogleSignInLayout, "No Internet. Can't Sign In.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snackbar.show();
                } else {
                    if(termsCheck.isChecked()) {
                        launchGoogleSignInIntent();
                    }else {
                        Toast.makeText(this, "Please accept terms of use before signing in", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.terms_of_use: {
                Intent i = new Intent(LoginActivity.this,TermsAndConditions.class);
                startActivity(i);
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
