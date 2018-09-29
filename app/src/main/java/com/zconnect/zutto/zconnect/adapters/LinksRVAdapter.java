package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.Links;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.ListItem;

import java.util.ArrayList;
import java.util.Vector;

public class LinksRVAdapter extends RecyclerView.Adapter<LinksRVAdapter.ProgrammingViewHolder>{

    Context context;
    Vector<ListItem> LinksList;

    public LinksRVAdapter(Vector<ListItem> LinksList, Context context)
    {
        this.LinksList=LinksList;
        this.context=context;
    }

    @Override
    public ProgrammingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.links_list_item_layout,parent,false);
        return new LinksRVAdapter.ProgrammingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProgrammingViewHolder holder, int position) {
        String title=LinksList.get(position).getTitle().toString();
        String link=LinksList.get(position).getLinkURL().toString();
        holder.link.setText(link);
        holder.title.setText(title);
        holder.link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browseIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(holder.link.getText().toString()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return LinksList.size();
    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder{
        TextView link;
        TextView title;
        public ProgrammingViewHolder(View itemView) {
            super(itemView);
            link=(TextView)itemView.findViewById(R.id.link);
            title=(TextView)itemView.findViewById(R.id.title);
        }
    }
}
