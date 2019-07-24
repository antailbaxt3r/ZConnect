package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

public class InternshipsDetails extends BaseActivity {

    TextView description,duration,question,role,stipend,organisaton;
    Button apply;
    String appliedCheck;

    Intent callingActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internships_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setToolbar();
        toolbar.setTitle("Internship Details");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        description = findViewById(R.id.internships_description);
        duration = findViewById(R.id.internships_duration);
        question = findViewById(R.id.internships_question);
        role = findViewById(R.id.internships_role);
        stipend = findViewById(R.id.internships_stipend);
        organisaton = findViewById(R.id.internships_organisation);
        apply = findViewById(R.id.applybutton);

        callingActivityIntent = getIntent();

        description.setText(callingActivityIntent.getStringExtra("description"));
        duration.setText(callingActivityIntent.getStringExtra("duration"));
        question.setText(callingActivityIntent.getStringExtra("question"));
        role.setText(callingActivityIntent.getStringExtra("role"));
        stipend.setText(callingActivityIntent.getStringExtra("stipend"));
        organisaton.setText(callingActivityIntent.getStringExtra("organization"));
        appliedCheck = callingActivityIntent.getStringExtra("appliedCheck");

        if (appliedCheck.equalsIgnoreCase("apply"))
        {
        apply.setOnClickListener(view -> {
                Intent applyForInternship = new Intent(this, ApplyInternships.class);
                applyForInternship.putExtra("organizationID",callingActivityIntent.getStringExtra("organizationID"));
                applyForInternship.putExtra("internshipID",callingActivityIntent.getStringExtra("internshipID"));
                applyForInternship.putExtra("question",callingActivityIntent.getStringExtra("question"));

                startActivity(applyForInternship);
            });
        }
        else
        {
            apply.setText("Applied");
            apply.setEnabled(false);
        }
    }
}
