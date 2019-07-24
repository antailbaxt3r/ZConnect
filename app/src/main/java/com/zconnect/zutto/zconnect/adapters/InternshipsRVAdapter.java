package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ApplyInternships;
import com.zconnect.zutto.zconnect.Internships;
import com.zconnect.zutto.zconnect.InternshipsDetails;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.InternshipsItemFormat;

import java.util.Vector;

public class InternshipsRVAdapter extends RecyclerView.Adapter<InternshipsRVAdapter.ViewHolder>{

    Context context;
    Vector<InternshipsItemFormat> internshipsList;
    DatabaseReference databaseReference;
    String communityReference;
    public InternshipsRVAdapter(Context context, Vector<InternshipsItemFormat> internshipsItemFormats,String communityReference)
    {
        this.context = context;
        this.internshipsList = internshipsItemFormats;
        this.communityReference = communityReference;
    }

    @Override
    public InternshipsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_internships, parent, false);

        return new InternshipsRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final InternshipsRVAdapter.ViewHolder holder, int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("internships").child("opportunities").child(internshipsList.get(position).getKey()).child("users");

        try {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        holder.apply.setText("Applied");
                        holder.apply.setBackground(ContextCompat.getDrawable(context,R.drawable.round_button_gray));
                        holder.apply.setTextColor(ContextCompat.getColor(context,R.color.primaryText));
                        holder.apply.setEnabled(false);
                        holder.apply.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.apply.setText("Apply");
                        holder.apply.setBackground(ContextCompat.getDrawable(context,R.drawable.round_button_primary));
                        holder.apply.setTextColor(ContextCompat.getColor(context,R.color.white));
                        holder.apply.setEnabled(true);
                        holder.apply.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e){ }

        holder.description.setText(internshipsList.get(position).getDescription());
        holder.duration.setText(internshipsList.get(position).getDuration());
        holder.role.setText(internshipsList.get(position).getRole());
        holder.organisaton.setText(internshipsList.get(position).getOrganization());
        holder.stipend.setText("₹" + internshipsList.get(position).getStipend().toString());
        holder.pos = position;
    }

    @Override
    public int getItemCount() {
        return internshipsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView description,duration,role,stipend,organisaton;
        int pos;
        Button apply;

        public ViewHolder(View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.internships_description);
            duration = itemView.findViewById(R.id.internships_duration);
            role = itemView.findViewById(R.id.internships_role);
            stipend = itemView.findViewById(R.id.internships_stipend);
            organisaton = itemView.findViewById(R.id.internships_organisation);
            apply = itemView.findViewById(R.id.applybutton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent internshipsDetails = new Intent(context, InternshipsDetails.class);
                    internshipsDetails.putExtra("description",internshipsList.get(pos).getDescription());
                    internshipsDetails.putExtra("organization",internshipsList.get(pos).getOrganization());
                    internshipsDetails.putExtra("role",internshipsList.get(pos).getRole());
                    internshipsDetails.putExtra("question",internshipsList.get(pos).getQuestion());
                    internshipsDetails.putExtra("duration",internshipsList.get(pos).getDuration());
                    internshipsDetails.putExtra("stipend",internshipsList.get(pos).getStipend());
                    internshipsDetails.putExtra("organizationID",internshipsList.get(pos).getOrgID());
                    internshipsDetails.putExtra("internshipID",internshipsList.get(pos).getKey());
                    internshipsDetails.putExtra("appliedCheck",apply.getText().toString());

                    context.startActivity(internshipsDetails);
                }
            });

            apply.setOnClickListener(view -> {
                Intent applyForInternship = new Intent(context, ApplyInternships.class);
                applyForInternship.putExtra("organizationID",internshipsList.get(pos).getOrgID());
                applyForInternship.putExtra("internshipID",internshipsList.get(pos).getKey());
                applyForInternship.putExtra("question",internshipsList.get(pos).getQuestion());

                context.startActivity(applyForInternship);
            });
        }
    }
}
