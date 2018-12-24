package com.zconnect.zutto.zconnect.pools;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.zconnect.zutto.zconnect.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentCaptureActivity extends AppCompatActivity {

    public static final String TAG = "PaymentCaptureActivity";
    private static final String RAZOR_PAY_KEY = "rzp_test_KXsdrhWl1Mp45s";
    private static final String RAZOR_PAY_SECRET = "RTnQaWurA8LsntSyWHPTrE4t";

    private LinearLayout into_view;
    private TextView transictionID, amount;

    private LinearLayout ll_progressBar;
    private TextView loading_text;

    private String tranID;
    private int total_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_capture);

        attachID();
        Bundle b = getIntent().getExtras();
        tranID = b.getString("paymentID");
        total_amount = b.getInt("amount");
        into_view.setVisibility(View.GONE);
        setProgressBarView(View.VISIBLE, "Do not Press Back Confirming Payment");
        capturetransection();


    }

    private void capturetransection() {
        //TODO should be run on server
        new MyAsyncTask().execute(" ");


    }

    private void attachID() {
        into_view = findViewById(R.id.ll_info);
        transictionID = findViewById(R.id.transection_id);
        amount = findViewById(R.id.total_pay_amount);
        ll_progressBar = findViewById(R.id.ll_progressBar);
        loading_text = findViewById(R.id.loading_text);
    }

    private void setProgressBarView(int visibility, String message) {
        ll_progressBar.setVisibility(visibility);
        loading_text.setText(message);

    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        Exception exception;

        MyAsyncTask() {
            super();
            this.exception = null;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                RazorpayClient razorpayClient = new RazorpayClient(RAZOR_PAY_KEY, RAZOR_PAY_SECRET);
                JSONObject options = new JSONObject();
                options.put("amount", total_amount * 100);
                razorpayClient.Payments.capture(tranID, options);
            } catch (RazorpayException e) {
                this.exception = e;
            } catch (JSONException e) {
                this.exception = e;

            }
            return "";

        }

        @Override
        protected void onPostExecute(String result) {
            if (this.exception != null) {
                setProgressBarView(View.VISIBLE, "An exception occure");

            } else {
                transictionID.setText("Transiction ID : " + tranID);
                amount.setText("Amount : " + String.valueOf(total_amount));
                into_view.setVisibility(View.VISIBLE);
                setProgressBarView(View.GONE, "");
            }
        }
    }
}
