package com.darshansfa.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.orm.SugarContext;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Models.DistributorResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.DistributorDialog;
import com.darshansfa.Utility.NotifyListener;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Distributor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends RuntimePermissionsActivity {

    private static final int REQUEST_PERMISSIONS = 22;
    @BindView(R.id.etUserName)
    EditText etUserName;

    @BindView(R.id.etPass)
    EditText etPass;

    @BindView(R.id.tv_msg)
    TextView tv_msg;

    TextView forgotPassword;

    //forgotPassword
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        SugarContext.init(LoginActivity.this);

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        LoginActivity.super.requestAppPermissions(new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, R.string.accept, REQUEST_PERMISSIONS);


//        SyncDialog syncDialog = new SyncDialog();
//        ArrayList<Integer> integers = new ArrayList<>();
//        integers.add(Constants.API.PARTS);
//        syncDialog.show(this, integers, new NotifyListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onCompleted() {
//                etUserName.setText("Complete");
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });


        new HttpAMAZON_TOKEN().execute();

    }

    private class HttpAMAZON_TOKEN extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UIUtil.startProgressDialog(LoginActivity.this, "Please wait..");
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                String access_url=  "http://pony.gladminds.co/pony/get-aws-info/?access_token=" +
                        PreferencesManger.getStringFields(LoginActivity.this, Constants.Pref.TOKEN);

                //String secret=obj.getJSONObject("data").getString("secret");
                //String key=obj.getJSONObject("data").getString("key");

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            UIUtil.stopProgressDialog(LoginActivity.this);
        }

    }

    private void forgotPassword() {
        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
        finish();
        //Toast.makeText(this, "ForgotPassword", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @OnClick(R.id.btnSingIn)
    public void signIn() {
        if (TextUtils.isEmpty(etUserName.getText().toString())) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etPass.getText().toString())) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet..!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", etUserName.getText().toString());
        jsonObject.addProperty("password", etPass.getText().toString());
        tv_msg.setVisibility(View.GONE);
        UIUtil.startProgressDialog(this, "Login.");
        Call<JsonObject> d = RetrofitAPI.getInstance().getApi().signIn(jsonObject);
        Log.e("re", "" + jsonObject.toString());
        d.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    UIUtil.stopProgressDialog(getApplicationContext());
                    JsonObject object = response.body();
                    Log.e("re", "" + response.body().toString());
                    if (1 == object.get("status").getAsInt()) {

                        PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.EMAIL, etUserName.getText().toString().trim());

                        PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.USERNAME, object.get("name").getAsString());
                        PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.TOKEN, object.get("token").getAsString());
                        PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.LOGIN_TYPE, object.get("login_type").getAsString());
                        if (Constants.DSR_LOGIN.equalsIgnoreCase(object.get("login_type").getAsString())) {
                            PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.DSR_ID, object.get("Id").getAsString());
                        } else {
                            PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.RETAILER_ID, object.get("Id").getAsString());
                        }
                        getDistributorList();
                    } else {
                        tv_msg.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Log.e("re", "" + t.toString());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });


    }


    private void getDistributorList() {

        UIUtil.startProgressDialog(this, "Getting depo list, Please wait....");
        Call<DistributorResponse> call = RetrofitAPI.getInstance().getApi().getDistributor(PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<DistributorResponse>() {
            @Override
            public void onResponse(Call<DistributorResponse> call, Response<DistributorResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    DistributorResponse distributorResponse = response.body();
                    if (distributorResponse.getStatus() == 1) {
                        Distributor.saveInTx(distributorResponse.getDistributor());
                        if (Constants.DSR_LOGIN.equalsIgnoreCase(PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.LOGIN_TYPE))) {
                            List<Distributor> list = Distributor.listAll(Distributor.class);
                            if (list.size() == 1) {
                                PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.DISTRIBUTOR_ID, list.get(0).getDistributorCode());
                                PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.DEPO_NAME, list.get(0).getName());
                                PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.DEPOT_CODE, list.get(0).getDepotCode());
                                startActivity(new Intent(getApplicationContext(), DSRDashboardActivity.class));
                                finish();
                                return;
                            }
                            DistributorDialog.show(LoginActivity.this, list, new NotifyListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onCompleted(String s) {
                                    PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.DISTRIBUTOR_ID, s);
                                    startActivity(new Intent(getApplicationContext(), DSRDashboardActivity.class));
                                    finish();
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });

                        } else {
                            Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DistributorResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
