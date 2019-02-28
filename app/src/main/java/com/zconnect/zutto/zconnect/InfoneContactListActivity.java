package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.adapters.InfoneContactsRVAdpater;
import com.zconnect.zutto.zconnect.addActivities.AddInfoneCat;
import com.zconnect.zutto.zconnect.addActivities.AddInfoneContact;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class InfoneContactListActivity extends AppCompatActivity {

    private static final int RQS_PICK_CONTACT = 1;

    Toolbar toolbar;
    String catId;
    RecyclerView recyclerViewContacts;
    ArrayList<InfoneContactsRVItem> contactsRVSearchItems;
    ArrayList<InfoneContactsRVItem> contactsRVItems = new ArrayList<>();
    InfoneContactsRVAdpater infoneContactsRVAdpater;
    DatabaseReference databaseReferenceList;
    ValueEventListener listener;
    FloatingActionButton fabAddContact;
    private SharedPreferences communitySP;
    public String communityReference;
    ArrayList<String> phoneNumbs;
    private final String TAG = getClass().getSimpleName();
    private String catName, catImageurl;
    private String catAdmin;
    ProgressBar progressBar;

    int totalContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_contact_list);
        toolbar=(Toolbar) findViewById(R.id.toolbar_app_bar_infone);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbarSetup();

        catId = getIntent().getExtras().getString("catId");
        catName = getIntent().getExtras().getString("catName");
        catImageurl = getIntent().getExtras().getString("catImageurl");
        catAdmin = getIntent().getExtras().getString("catAdmin");

        toolbar.setTitle(catName);
        setTitle(catName);
        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceList = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("infone").child("categories").child(catId);
        progressBar = (ProgressBar) findViewById(R.id.infone_contact_list_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewContacts = (RecyclerView) findViewById(R.id.rv_infone_contacts);
        recyclerViewContacts.setVisibility(View.GONE);
        fabAddContact = (FloatingActionButton) findViewById(R.id.fab_contacts_infone);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, RQS_PICK_CONTACT);
            }
        });

        setAdapter("lite",false);
    }

    private  void setAdapter(final String queryString, final Boolean search) {

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                contactsRVItems = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

//                    try {
                        InfoneContactsRVItem temp = new InfoneContactsRVItem();

                        String name = childSnapshot.child("name").getValue(String.class);
                        String imageThumb = childSnapshot.child("thumbnail").getValue(String.class);
                        String infoneUserId = childSnapshot.getKey();
                        String desc = childSnapshot.child("desc").getValue(String.class);
                        temp.setName(name);
                        temp.setImageThumb(imageThumb);
                        temp.setInfoneUserId(infoneUserId);
                        temp.setDesc(desc);


                        Log.e("tt", "data " + name);
                        Log.e("tt", "data " + imageThumb);

                        phoneNumbs = new ArrayList<>();

                        for (DataSnapshot grandChildShot :
                                childSnapshot.child("phone").getChildren()) {
                            try {
                                phoneNumbs.add(grandChildShot.getValue(String.class));
                            } catch (Exception e) {
                            }

                            Log.e("tt", "data " + phoneNumbs.toString());
                        }

                        temp.setPhoneNums(phoneNumbs);

                        Boolean contactHidden = false;

                        if(childSnapshot.hasChild("contactHidden")){
                            contactHidden = childSnapshot.child("contactHidden").getValue(Boolean.class);
                        }

                        temp.setContactHidden(contactHidden);

                        contactsRVItems.add(temp);

                        totalContacts = contactsRVItems.size();
//                    }catch (Exception e){}
                }

                Collections.sort(contactsRVItems, new Comparator<InfoneContactsRVItem>() {
                    @Override
                    public int compare(InfoneContactsRVItem contact1, InfoneContactsRVItem contact2) {
                        return contact1.getName().trim().compareToIgnoreCase(contact2.getName().trim());
                    }
                });


                infoneContactsRVAdpater = new InfoneContactsRVAdpater(InfoneContactListActivity.this, contactsRVItems, catId);
                recyclerViewContacts.setAdapter(infoneContactsRVAdpater);
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneContactListActivity.class.getName(), "database error" + databaseError.toString());
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };

        if(search){
            if(!queryString.equals("")) {
                contactsRVSearchItems = new ArrayList<>();
                for (int i = 0; i < contactsRVItems.size(); i++) {

                    if (contactsRVItems.get(i).getName().toLowerCase().trim().contains(queryString.toLowerCase())) {
                        contactsRVSearchItems.add(contactsRVItems.get(i));
                    }
                    if (contactsRVSearchItems.size() > 7) {
                        break;
                    }
                }

                infoneContactsRVAdpater = new InfoneContactsRVAdpater(InfoneContactListActivity.this, contactsRVSearchItems, catId);
                recyclerViewContacts.setAdapter(infoneContactsRVAdpater);
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
            }else {
                infoneContactsRVAdpater = new InfoneContactsRVAdpater(InfoneContactListActivity.this, contactsRVItems, catId);
                recyclerViewContacts.setAdapter(infoneContactsRVAdpater);
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
            }

        }else {
            databaseReferenceList.addValueEventListener(listener);
        }

    }

    private void addContact(String contactName, String contactNumber) {

        Intent addContactIntent = new Intent(this, AddInfoneContact.class);
        addContactIntent.putExtra("catId", catId);
        addContactIntent.putExtra("catImageURL",catImageurl);
        addContactIntent.putExtra("catName", catName);
        addContactIntent.putExtra("totalContacts",totalContacts);
        addContactIntent.putExtra("contactName", contactName);
        addContactIntent.putExtra("contactNumber", contactNumber);
//        addContactIntent.putExtra("contactPhoto", contactPhoto);
        startActivity(addContactIntent);

    }

    private void editCategory() {

        Intent editCatIntent=new Intent(InfoneContactListActivity.this,AddInfoneCat.class);
        editCatIntent.putExtra("catId",catId);
        editCatIntent.putExtra("catName",catName);
        editCatIntent.putExtra("catImageurl",catImageurl);
        startActivity(editCatIntent);

    }

    private void toolbarSetup() {
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        //databaseReferenceList.removeEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceList.removeEventListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_infone_contact_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setAdapter(query,true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setAdapter(newText,true);
                return false;
            }
        });


        MenuItem menuItem = menu.findItem(R.id.search);

        MenuItemCompat.setOnActionExpandListener(menuItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("catID",catId);

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_SEARCH);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setAdapter("lite",false);
                //Toast.makeText(InfoneContactListActivity.this, "Collapsed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {

            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(catAdmin)) {
                editCategory();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQS_PICK_CONTACT) {
            if(resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                String number = "";
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {


                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        phones.moveToFirst();
                        number = phones.getString(phones.getColumnIndex("data1"));
                        number = number.substring(number.indexOf(' ')+1);
                        number = number.replace(' ', '\0');
                    }
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

//                    Bitmap photo = null;
//
//                    try {
//                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getApplicationContext().getContentResolver(),
//                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
//
//                        if (inputStream != null) {
//                            photo = BitmapFactory.decodeStream(inputStream);
//                        }
//
//                        assert inputStream != null;
//                        inputStream.close();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    if(number.length()<1)
                        Toast.makeText(getApplicationContext(), "Contact does not have a number.", Toast.LENGTH_SHORT).show();
                    else
                        addContact(name, number);
                }
            }
        }
    }
}
