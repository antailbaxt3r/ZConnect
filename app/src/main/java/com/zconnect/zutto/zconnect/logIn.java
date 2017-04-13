package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class logIn extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //    private EditText emailText;
//    private EditText passwordText;
//    private Button newUserButton;
//    private Button logInButton;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgress;
    private SignInButton mGoogleSignIn;
    private int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        newUserButton = (Button) findViewById(R.id.register);
//        logInButton = (Button) findViewById(R.id.login);
//        emailText = (EditText) findViewById(R.id.email);
//        passwordText = (EditText) findViewById(R.id.password);
        mGoogleSignIn = (SignInButton) findViewById(R.id.GoogleSignIn);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mProgress = new ProgressDialog(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null)
                {
                    if(firebaseAuth.getCurrentUser().getEmail().toString().endsWith("@goa.bits-pilani.ac.in")) {
                        checkUser();
                    }else {

                        logout();
                        mProgress.dismiss();
                        Toast.makeText(logIn.this, "Login through your BITS email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

//        logInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!isNetworkAvailable(getApplicationContext())) {
//
//                    Snackbar snack = Snackbar.make(mGoogleSignIn, "No Internet. Can't Log In.", Snackbar.LENGTH_LONG);
//                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
//                    snackBarText.setTextColor(Color.WHITE);
//                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
//                    snack.show();
//
//                } else {
//                    startLogin();
//                }
//
//            }
//        });

//        newUserButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent newUserIntent = new Intent(logIn.this,registerNewUser.class);
//                startActivity(newUserIntent);
//            }
//        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable(getApplicationContext())) {

                    Snackbar snack = Snackbar.make(mGoogleSignIn, "No Internet. Can't Sign In.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                    snack.show();

                } else {
                    signIn();
                }



            }
        });
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
                // ...
                mProgress.dismiss();
                Snackbar snack = Snackbar.make(mGoogleSignIn, "Login failed", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();
                //Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();

            }

        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                            Snackbar snack = Snackbar.make(mGoogleSignIn, "Authentication failed.", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                            snack.show();
                            mProgress.dismiss();
                        }
//                        else {
//                         mProgress.dismiss();
//
//                            checkUser();
//                            // ...
//                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
public boolean isNetworkAvailable(final Context context) {
    final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
    return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
}
//    private void startLogin(){
//
//        String email = emailText.getText().toString().trim();
//        String password = passwordText.getText().toString().trim();
//
//
//        mProgress.setMessage("Signing In..");
//
//        if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
//        {
//            mProgress.show();
//            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                    if(task.isSuccessful()){
//
//                        checkUser();
//
//                    }else {
//                        mProgress.dismiss();
//                        Snackbar snack = Snackbar.make(mGoogleSignIn, "Email or Password Incorrect", Snackbar.LENGTH_LONG);
//                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
//                        snackBarText.setTextColor(Color.WHITE);
//                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
//                        snack.show();
//                        //  Toast.makeText(logIn.this, "Email or Password Incorrect", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        }else{
//            Snackbar snack = Snackbar.make(mGoogleSignIn, "Enter both Fields", Snackbar.LENGTH_LONG);
//            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
//            snackBarText.setTextColor(Color.WHITE);
//            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
//            snack.show();
//            // Toast.makeText(this, "Enter both Fields", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    private void logout() {
        mAuth.signOut();

//         Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }


    private void checkUser() {
        if (mAuth.getCurrentUser() != null) {
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        mProgress.dismiss();
                        Intent loginIntent = new Intent(logIn.this, home.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                        finish();

                    } else {
                        mProgress.dismiss();
                        Intent loginIntent = new Intent(logIn.this, setDetails.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        loginIntent.putExtra("caller", "login");
                        startActivity(loginIntent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    if (databaseError.getCode() == -3 && mAuth.getCurrentUser() != null) {

                        logout();
                        Toast.makeText(logIn.this, "Login through your BITS email", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
