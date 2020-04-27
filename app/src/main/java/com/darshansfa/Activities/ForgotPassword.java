package com.darshansfa.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.R;
import com.darshansfa.Utility.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    EditText etUserName;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etUserName=(EditText)findViewById(R.id.etUserName);
        submit=(Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UIUtil.isInternetAvailable(ForgotPassword.this)) {
                    Toast.makeText(ForgotPassword.this, "No internet..!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!(etUserName.getText().toString().trim().matches(""))){
                    UIUtil.startProgressDialog(getApplicationContext(), "Please wait...");
                    submit.setEnabled(false);
                    forgotPass();
                }else{
                    Toast.makeText(ForgotPassword.this, "Enter Username", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void forgotPass() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", etUserName.getText().toString().trim());

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().forgotPassword(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    Log.e("res", "----------" + response.body());

                    JsonObject object = response.body();

                    if (object.get("status").getAsInt() == 1) {

                        Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        submit.setEnabled(true);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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
               // Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
