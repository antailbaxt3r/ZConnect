package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

public class PhonebookDetails extends AppCompatActivity {
    String name, number, email, desc, imagelink;
    private android.support.design.widget.TextInputEditText editTextName;
    private android.support.design.widget.TextInputEditText editTextEmail;
    private android.support.design.widget.TextInputEditText editTextDetails;
    private android.support.design.widget.TextInputEditText editTextNumber;
    private SimpleDraweeView image;
    private ImageView mail, call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonebook_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        image = (SimpleDraweeView) findViewById(R.id.contact_details_display_image);
        editTextDetails = (TextInputEditText) findViewById(R.id.contact_details_editText_1);
        editTextEmail = (TextInputEditText) findViewById(R.id.contact_details_email_editText);
        editTextName = (TextInputEditText) findViewById(R.id.contact_details_name_editText);
        editTextNumber = (TextInputEditText) findViewById(R.id.contact_details_number_editText);
        call = (ImageView) findViewById(R.id.callbutton);
        mail = (ImageView) findViewById(R.id.mailbutton);
        setSupportActionBar(toolbar);
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
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("desc");
        number = getIntent().getStringExtra("number");
        imagelink = getIntent().getStringExtra("image");
        email = getIntent().getStringExtra("email");
        if (name != null && desc != null && number != null && imagelink != null && email != null) {
            editTextName.setText(name);
            editTextDetails.setText(desc);
            editTextNumber.setText(number);
            editTextNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
                }
            });
            editTextEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
                }
            });
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });
            image.setImageURI((Uri.parse(imagelink)));
            editTextEmail.setText(email);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phonebook_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_report) {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "zconnectinc@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Problem with the content displayed");
            // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

