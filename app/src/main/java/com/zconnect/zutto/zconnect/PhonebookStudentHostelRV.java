package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.ItemFormats.PhonebookStudentHostelItem;

import java.util.Vector;

/**
 * Created by shubhamk on 9/2/17.
 */

public class PhonebookStudentHostelRV extends RecyclerView.Adapter<PhonebookStudentHostelRV.ViewHolder> {
    Context context;

    Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems;

    public PhonebookStudentHostelRV(Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems, Context context) {
        this.phonebookStudentHostelItems = phonebookStudentHostelItems;
        this.context = context;
    }

    @Override
    public PhonebookStudentHostelRV.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.student_hostel_list_item_format, parent, false);

        // Return a new holder instance
        return new PhonebookStudentHostelRV.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(PhonebookStudentHostelRV.ViewHolder holder, int position) {
        holder.hostel.setText(phonebookStudentHostelItems.get(position).getHostel());
    }

    @Override
    public int getItemCount() {
        return phonebookStudentHostelItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView hostel;

        public ViewHolder(View itemView) {
            super(itemView);
            hostel = (TextView) itemView.findViewById(R.id.student_hostel_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cat = phonebookStudentHostelItems.get(getAdapterPosition()).getCat();
                    String hostel = phonebookStudentHostelItems.get(getAdapterPosition()).getHostel();
                    String category = cat.equals("A") ? "Admin" : (cat.equals("O") ? "others" : "Students");
                    CounterManager.infoneOpenCategory(category, hostel);

                    Intent intent = new Intent(context, PhonebookHostelWise.class);
                    intent.putExtra("Cat", cat);
                    intent.putExtra("Hostel", hostel);
                    context.startActivity(intent);
                    if (context instanceof PhonebookHostelWise) {
                        ((PhonebookHostelWise) context).finish();
                    }
                }
            });

        }
    }

}
