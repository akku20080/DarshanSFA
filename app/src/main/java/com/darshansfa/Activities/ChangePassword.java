package com.darshansfa.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    EditText etOldPassword,etNewPass,etConNewPass;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etOldPassword=(EditText)findViewById(R.id.etOldPassword);
        etNewPass=(EditText)findViewById(R.id.etNewPass);
        etConNewPass=(EditText)findViewById(R.id.etConNewPass);

        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UIUtil.isInternetAvailable(ChangePassword.this)) {
                    Toast.makeText(ChangePassword.this, "No internet..!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etOldPassword.getText().toString())) {
                    Toast.makeText(ChangePassword.this, "Enter old password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etNewPass.getText().toString())) {
                    Toast.makeText(ChangePassword.this, "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etConNewPass.getText().toString())) {
                    Toast.makeText(ChangePassword.this, "Enter confirm new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(etNewPass.getText().toString().trim().matches(etConNewPass.getText().toString().trim()))) {
                    Toast.makeText(ChangePassword.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                changePass();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void changePass() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", PreferencesManger.getStringFields(this, Constants.Pref.EMAIL));
        jsonObject.addProperty("old_password", etOldPassword.getText().toString().trim());
        jsonObject.addProperty("new_password", etNewPass.getText().toString().trim());

        Log.e("jsonObject", "----------"+jsonObject.toString());

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().changePassord(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    Log.e("res", "----------" + response.body());


                    JsonObject object = response.body();

                    if (object.get("status").getAsInt() == 1) {

                        Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        submit.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    submit.setEnabled(true);
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                submit.setEnabled(true);
                t.printStackTrace();
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
