package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = getClass().getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDbRef;
    private ProgressDialog mProgress;
    @BindView(R.id.GoogleSignIn)
    Button mGoogleSignInBtn;
    @BindView(R.id.guestLogIn)
    Button mGuestLogInBtn;
    @BindView(R.id.bitsgoaemailinfo)
    TextView bpgcEmailInfo;
    private Typeface ralewayLight;
    private Typeface ralewayMedium;
    private int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersDbRef.keepSynced(true);
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

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            userEmail = mUser.getEmail();
            if (userEmail != null && userEmail.endsWith("@goa.bits-pilani.ac.in")) {
                checkUser(mUser);
            } else {
                logout();
                mProgress.dismiss();
                Toast.makeText(LoginActivity.this, "Login through your BITS email", Toast.LENGTH_SHORT).show();
            }
        }
        mGuestLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guestLogIn(true);
                Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        mGoogleSignInBtn = (Button) findViewById(R.id.GoogleSignIn);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGoogleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Snackbar snack = Snackbar.make(mGoogleSignInBtn, "No Internet. Can't Sign In.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                    snack.show();
                } else {
                    signIn();
                }
            }
        });

        ralewayLight = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        ralewayMedium = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        bpgcEmailInfo.setTypeface(ralewayLight);
        mGoogleSignInBtn.setTypeface(ralewayMedium);
        mGuestLogInBtn.setTypeface(ralewayMedium);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("Signing in...");
            mProgress.show();
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                mProgress.dismiss();
                Snackbar snack = Snackbar.make(mGoogleSignInBtn, "Login failed", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();
            }
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
                            Snackbar snack = Snackbar.make(mGoogleSignInBtn, "Authentication failed.", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                            snack.show();
                            mProgress.dismiss();
                        } else {
                            mUser = mAuth.getCurrentUser();
                            if (mUser != null
                                    && (userEmail = mUser.getEmail()) != null
                                    && userEmail.endsWith("@goa.bits-pilani.ac.in")) {
                                checkUser(mUser);
                            } else {
                                logout();
                                mProgress.dismiss();
                                Toast.makeText(LoginActivity.this, "Login through your BITS email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    public void guestLogIn(Boolean mode) {
        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        SharedPreferences.Editor editInfo = sharedPref.edit();
        editInfo.putBoolean("mode", mode);
        editInfo.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        guestLogIn(false);
    }

    private void logout() {
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    private void checkUser(@NonNull final FirebaseUser user) {
        usersDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference currentUserDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
                currentUserDbRef.child("Image").setValue(mUser.getPhotoUrl());
                currentUserDbRef.child("Username").setValue(user.getDisplayName());
                currentUserDbRef.child("Email").setValue(mUser.getEmail());
                Intent editProfileIntent = new Intent(LoginActivity.this, EditProfile.class);
                editProfileIntent.putExtra("caller", TAG);
                editProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(editProfileIntent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (databaseError.getCode() == -3 && mAuth.getCurrentUser() != null) {
                    logout();
                    Toast.makeText(LoginActivity.this, "Login through your BITS email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: error message is " + connectionResult.getErrorMessage());
    }
}
