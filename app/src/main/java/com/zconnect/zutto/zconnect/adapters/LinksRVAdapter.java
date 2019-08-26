package com.zconnect.zutto.zconnect.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.WebViewActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ListItem;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

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
        String title=LinksList.get(position).getLinkTitle().toString();
        String link=LinksList.get(position).getLinkURL().toString();
        holder.link.setText(link);
        holder.title.setText(title);
        holder.link.setOnClickListener(view -> {
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//            intentBuilder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
            intentBuilder.setExitAnimations(context, android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            String linkUrl = LinksList.get(position).getLinkURL();
            if(!linkUrl.startsWith("http://") && !linkUrl.startsWith("https://"))
            {
                LinksList.get(position).setLinkURL("http://"+linkUrl);
            }
            customTabsIntent.launchUrl(context, Uri.parse(LinksList.get(position).getLinkURL()));
//            intent.putExtra("url",LinksList.get(position).getLinkURL());
//            intent.putExtra("title",LinksList.get(position).getTitle());
//            context.startActivity(intent);

            CounterItemFormat counterItemFormat = new CounterItemFormat();
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_LINKS_CLICK);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();
        });
        holder.copyIcon.setOnClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("link", link);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }




    @Override
    public int getItemCount() {
        return LinksList.size();
    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder{
        private TextView link;
        private TextView title;
        private ImageView copyIcon;
        public ProgrammingViewHolder(View itemView) {
            super(itemView);
            link=(TextView)itemView.findViewById(R.id.link);
            title=(TextView)itemView.findViewById(R.id.title);
            copyIcon = itemView.findViewById(R.id.link_copy_to_clipboard);
        }
    }
}
