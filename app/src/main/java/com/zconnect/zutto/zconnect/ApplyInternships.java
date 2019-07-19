package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

import java.util.HashMap;

public class ApplyInternships extends BaseActivity {
    Intent callingActivityIntent,intent;
    String orgID,internshipID,internshipQuestion,nameOfUserString;
    DatabaseReference databaseReference,databaseReferenceUsers1;
    EditText branch,proficiency,answer;
    TextView nameOfUser;
    TextView question;
    Button done;
    RadioButton male,female,others;
    RadioGroup radioGroup;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_internships);

        branch = findViewById(R.id.branch);
        proficiency = findViewById(R.id.proficiency);
        answer = findViewById(R.id.answer);

        nameOfUser = findViewById(R.id.name);
        question = findViewById(R.id.question);

        done = findViewById(R.id.done);
        male = findViewById(R.id.radioButton1);
        female = findViewById(R.id.radioButton2);
        others = findViewById(R.id.radioButton3);
        radioGroup = findViewById(R.id.radiogroup);

        callingActivityIntent = getIntent();

        intent = new Intent(this,Internships.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        orgID = callingActivityIntent.getStringExtra("organizationID");
        internshipID = callingActivityIntent.getStringExtra("internshipID");
        internshipQuestion = callingActivityIntent.getStringExtra("question");

        nameOfUserString = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("appliedInternships").child(orgID).child(internshipID).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReferenceUsers1 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nameOfUser.setText(nameOfUserString);

        //Progress bar needed
        databaseReferenceUsers1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("gender"))
                {
                    if (dataSnapshot.child("gender").getValue().toString().equalsIgnoreCase("male"))
                        male.setChecked(true);
                    else if (dataSnapshot.child("gender").getValue().toString().equalsIgnoreCase("female"))
                        female.setChecked(true);
                    else
                        others.setChecked(true);

                }
                if (dataSnapshot.hasChild("branch"))
                    branch.setText(dataSnapshot.child("branch").getValue().toString());
                if (dataSnapshot.hasChild("proficiency"))
                    proficiency.setText(dataSnapshot.child("proficiency").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        question.setText(internshipQuestion);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DatabaseReference newPush = databaseReference.push();

                HashMap<String, Object> internshipMap = new HashMap<>();
                internshipMap.put("name",nameOfUserString);
                internshipMap.put("gender",gender);
                internshipMap.put("branch",branch.getText().toString());
                internshipMap.put("proficiency",proficiency.getText().toString());
                internshipMap.put("answer",answer.getText().toString());

                HashMap<String, Object> deatilsMap = new HashMap<>();

                gender = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();

                deatilsMap.put("gender",gender);
                deatilsMap.put("branch",branch.getText().toString());
                deatilsMap.put("proficiency",proficiency.getText().toString());

                databaseReference.setValue(internshipMap);
                databaseReferenceUsers1.updateChildren(deatilsMap);
                //Log.e("Database Message","Data pushed successfully");
                /*databaseReference.child("name").setValue(nameOfUserString);
                databaseReference.child("gender").setValue(gender.getText().toString());
                databaseReference.child("branch").setValue(branch.getText().toString());
                databaseReference.child("proficiency").setValue(proficiency.getText().toString());
                databaseReference.child("answer").setValue(answer.getText().toString());*/
                startActivity(intent);
                finish();
            }
        });
    }
}
