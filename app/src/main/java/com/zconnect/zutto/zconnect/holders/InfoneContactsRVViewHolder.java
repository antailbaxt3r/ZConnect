package com.zconnect.zutto.zconnect.holders;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.R;

/**
 * Created by tanmay on 24/3/18.
 */

public class InfoneContactsRVViewHolder extends RecyclerView.ViewHolder {

    public TextView nametv;
    public TextView viewstv;
    public ImageButton callImageBtn;
    public LinearLayout linearLayout;
    public SimpleDraweeView userAvatar;
    public TextView desctv;
    public TextView hiddentv;
    public Dialog verifyDialog;
    public  Button dialogVerifyYesbtn;
    public Button dialogVerifyNobtn;
    public MaterialEditText dialogVerifyphoneEt;
    public MaterialEditText dialogVerifyNameEt;
    public SimpleDraweeView dialogVerifyProfileImg;
    public Dialog requestCallDialog;
    public  Button dialogRequestCall1btn;
    public Button dialogRequestCall2btn;
    public MaterialEditText dialogRequestCallNameEt;
    public SimpleDraweeView dialogRequestCallProfileImg;
    public ImageButton whatsAppImageBtn;


    public InfoneContactsRVViewHolder(View itemView) {
        super(itemView);

        nametv = (TextView) itemView.findViewById(R.id.tv_name_infone_contacts);
        viewstv = (TextView) itemView.findViewById(R.id.tv_views_infone_contacts);
        callImageBtn = (ImageButton) itemView.findViewById(R.id.image_btn_call_infone_contacts);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_hori_infone_contacts);
        userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.image_infone_contacts);
        desctv = (TextView) itemView.findViewById(R.id.tv_desc_infone_contacts);
        hiddentv = (TextView) itemView.findViewById(R.id.tv_hidden_infone_contacts);
        verifyDialog =new Dialog(itemView.getContext());
        verifyDialog.setContentView(R.layout.dialog_validate_number);
        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogVerifyYesbtn = verifyDialog.findViewById(R.id.validate_infone_yes_btn);
        dialogVerifyNobtn = verifyDialog.findViewById(R.id.validate_infone_no_btn);
        dialogVerifyNameEt = verifyDialog.findViewById(R.id.et_name_infone_profile);
        dialogVerifyphoneEt = verifyDialog.findViewById(R.id.et_phone1_infone_profile);
        dialogVerifyProfileImg = verifyDialog.findViewById(R.id.image_profile_infone);

        requestCallDialog =new Dialog(itemView.getContext());
        requestCallDialog.setContentView(R.layout.dialog_request_call);
        requestCallDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogRequestCall1btn = requestCallDialog.findViewById(R.id.et_phone1_infone_profile);
        dialogRequestCall2btn = requestCallDialog.findViewById(R.id.et_phone2_infone_profile);
        dialogRequestCallNameEt = requestCallDialog.findViewById(R.id.et_name_infone_profile);
        dialogRequestCallProfileImg = requestCallDialog.findViewById(R.id.image_profile_infone);
        whatsAppImageBtn = itemView.findViewById(R.id.image_btn_whatsapp_infone_contacts) ;
    }
}
