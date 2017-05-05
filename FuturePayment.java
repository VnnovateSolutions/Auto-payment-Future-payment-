package com.recoverrefuel.Activities;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.recoverrefuel.Model.NotificationsSatus.NotificationsStatus;
import com.recoverrefuel.R;
import com.recoverrefuel.Utils.PayPalConfig;
import com.recoverrefuel.Utils.UIUtil;
import com.recoverrefuel.Utils.UserDetail;
import com.recoverrefuel.WebServices.ApiHandler;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by abc on 13-12-2016.
 */
public class ActivityChooseYourSubscriptionNew extends AppCompatActivity {
    private String team_id="",list_coach="",sport_name="",image_url="",team_name = "";
    private TextView txt_title;
    private TextView txt_back;
    int counter = 0;
    CheckBox cb_monthly,cb_yearly;
    TextView txt_pay;
    String  authorization_code="";


    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId(PayPalConfig.PAYPAL_CLIENT_ID)

            // Minimally, you will need to set three merchant information properties.
            // These should be the same values that you provided to PayPal when you registered your app.
            .merchantName("test")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    private String paymentAmount = "1.99";

    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    public static final int REQUEST_CODE_FUTURE_PAYMENT = 456;
    String monthly_amount="",yearly_amount="";
    ToggleButton toogle_subscribe,toogle_autopay;
    String metadata_id= "";
    TextView txt_terms;
    CheckBox cb_terms;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesubscription_new);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().hide();

        idMappings();

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);


        startService(intent);
        metadata_id= PayPalConfiguration.getApplicationCorrelationId(this);
        Log.e("metadataid",""+metadata_id);


        getData();
        setListeners();


    }



    private void idMappings()
    {
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_title.setText("Payment");
        txt_back = (TextView) findViewById(R.id.txt_back);
        txt_back.setText("Menu");

        toogle_subscribe = (ToggleButton) findViewById(R.id.toogle_subscribe);
        toogle_autopay = (ToggleButton) findViewById(R.id.toogle_autopay);
        txt_pay = (TextView)findViewById(R.id.txt_pay);
        cb_terms = (CheckBox)findViewById(R.id.cb_terms);
        txt_terms =(TextView)findViewById(R.id.txt_terms);

        String one = "Yes, I have read the usage ";
        Log.e("one",""+one.length());

        String two = "terms and conditions.";
        Log.e("two",""+two.length());


       
        ss.setSpan(clickableSpan, 27, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_terms.setText(ss);
        txt_terms.setMovementMethod(LinkMovementMethod.getInstance());
        txt_terms.setHighlightColor(Color.TRANSPARENT);
        if(MenuListing.monthly_plan.equalsIgnoreCase("yes")){
            toogle_subscribe.setChecked(true);

        }
        if(MenuListing.auto_pay.equalsIgnoreCase("yes")){
            toogle_autopay.setChecked(true);

        }
        checkDisablestatus();
    }

    private void getData()
    {

        Intent intent = getIntent();
        Bundle extas = intent.getExtras();
        if(extas!= null){



        }


    }









    private void setListeners()

    {


        txt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        toogle_subscribe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                checkDisablestatus();
            }
        });
        toogle_autopay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                checkDisablestatus();
            }
        });

        cb_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                checkDisablestatus();
            }
        });

//

        txt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(!cb_terms.isChecked()){
                    Toast.makeText(ActivityChooseYourSubscriptionNew.this,"Please accept terms and conditions.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!toogle_subscribe.isChecked()){

                    Toast.makeText(ActivityChooseYourSubscriptionNew.this,"Please select subscription plan",Toast.LENGTH_SHORT).show();
                    return;
                }


                if(toogle_subscribe.isChecked() ||toogle_autopay.isChecked()){
                 //   getPayment();

                    if(toogle_autopay.isChecked()){


                        getFuturepayment();
                        return;
                    }
                    else{
                        getPayment();
                    }




                }
                else{
                    Toast.makeText(ActivityChooseYourSubscriptionNew.this,"Please select subscription plan",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void checkDisablestatus() {

        if(!cb_terms.isChecked()){

            txt_pay.setBackground(getResources().getDrawable(R.drawable.gray_buttonpay));
            txt_pay.setTextColor(getResources().getColor(R.color.black_disable));
            return;
        }
        if(!toogle_subscribe.isChecked()){

            txt_pay.setBackground(getResources().getDrawable(R.drawable.gray_buttonpay));
            txt_pay.setTextColor(getResources().getColor(R.color.black_disable));
            return;
        }
        txt_pay.setBackground(getResources().getDrawable(R.drawable.orange_buttonpay));
        txt_pay.setTextColor(getResources().getColor(R.color.white));

    }

    private void getFuturepayment() {


        Intent intent = new Intent(ActivityChooseYourSubscriptionNew.this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }
    private void getPayment() {

        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null)
                {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.e("paymentExample", paymentDetails);

                        callAceptAPI();


                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }

        else {
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    //Getting the payment details
                    try {
                        //Getting the payment details
                       authorization_code = auth.getAuthorizationCode();
                        Log.e("authcode",""+authorization_code);

                        callAceptAutopayAPI();



                    } catch (Exception e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }



                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void callAceptAPI() {


        
    }



    private void callAceptAutopayAPI() {




      


    }



  



}
