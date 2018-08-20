package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.holders.InfoneContactsRVViewHolder;
import com.zconnect.zutto.zconnect.InfoneProfileActivity;
import com.zconnect.zutto.zconnect.itemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneContactsRVAdpater extends RecyclerView.Adapter<InfoneContactsRVViewHolder> {

    Context context;
    ArrayList<InfoneContactsRVItem> infoneContactsRVItems;
    String catId;

    public InfoneContactsRVAdpater(Context context, ArrayList<InfoneContactsRVItem> infoneContactsRVItems, String catId) {
        this.context = context;
        this.infoneContactsRVItems = infoneContactsRVItems;
        this.catId=catId;

    }

    @Override
    public InfoneContactsRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_infone_contacts, parent, false);

        return new InfoneContactsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoneContactsRVViewHolder holder, final int position) {

        holder.nametv.setText(infoneContactsRVItems.get(position).getName());
        holder.viewstv.setText(infoneContactsRVItems.get(position).getViews());
        if (infoneContactsRVItems.get(position).getImageThumb() != null) {
            Uri imageuri = Uri.parse(infoneContactsRVItems.get(position).getImageThumb());
            holder.userAvatar.setImageURI(imageuri);
        }
        final ArrayList<String> phoneNums = infoneContactsRVItems.get(position).getPhoneNums();

        holder.callImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterManager.infoneCallContact();
                callOptionsDialog(phoneNums);

            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profileIntent = new Intent(context, InfoneProfileActivity.class);
                profileIntent.putExtra("infoneUserId", infoneContactsRVItems.get(position).getInfoneUserId());
                profileIntent.putExtra("catId", catId);
                context.startActivity(profileIntent);
                CounterManager.infoneOpenContact(catId, infoneContactsRVItems.get(position).getInfoneUserId());
            }
        });

    }

    private void callOptionsDialog(final ArrayList<String> phoneArrayList) {


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(android.R.drawable.ic_menu_call);
        builderSingle.setTitle("Select to call: ");


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);

        arrayAdapter.addAll(phoneArrayList);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = phoneArrayList.get(which);

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + strName));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider cal
                    // ling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
                Toast.makeText(context, "call being made to " + strName, Toast.LENGTH_SHORT).show();
            }
        });
        builderSingle.show();

    }

    @Override
    public int getItemCount() {
        return infoneContactsRVItems.size();
    }
}
