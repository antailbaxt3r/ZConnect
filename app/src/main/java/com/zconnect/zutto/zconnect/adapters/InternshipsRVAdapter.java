package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.Internships;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.InternshipsItemFormat;

import java.util.Vector;

public class InternshipsRVAdapter extends RecyclerView.Adapter<InternshipsRVAdapter.ViewHolder>{

    Context context;
    Vector<InternshipsItemFormat> internshipsList;
    public InternshipsRVAdapter(Context context, Vector<InternshipsItemFormat> internshipsItemFormats)
    {
        this.context = context;
        this.internshipsList = internshipsItemFormats;
    }

    @Override
    public InternshipsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.internships_item_format, parent, false);

        return new InternshipsRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(final InternshipsRVAdapter.ViewHolder holder, int position) {
        holder.description.setText("Description : "+internshipsList.get(position).getDescription());
        holder.duration.setText("Duration : "+internshipsList.get(position).getDuration());
        holder.question.setText("Question : "+internshipsList.get(position).getQuestion());
        holder.role.setText("Role : "+internshipsList.get(position).getRole());
        holder.organisaton.setText("Organization : "+internshipsList.get(position).getOrganization());
        holder.stipend.setText("Stipend : "+internshipsList.get(position).getStipend().toString());

    }

    @Override
    public int getItemCount() {
        return internshipsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView description,duration,question,role,stipend,organisaton;
        Button apply;

        public ViewHolder(View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.internships_description);
            duration = itemView.findViewById(R.id.internships_duration);
            question = itemView.findViewById(R.id.internships_question);
            role = itemView.findViewById(R.id.internships_role);
            stipend = itemView.findViewById(R.id.internships_stipend);
            organisaton = itemView.findViewById(R.id.internships_organisation);
            apply = itemView.findViewById(R.id.applybutton);

            apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apply.setText("Applied");
                    apply.setEnabled(false);
                }
            });
        }
    }
}
