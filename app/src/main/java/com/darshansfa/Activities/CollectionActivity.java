package com.darshansfa.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.CollectionRecyclerAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.ImageUtil;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Cheque;
import com.darshansfa.dbModel.Invoice;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionActivity extends AppCompatActivity {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2;

    @BindView(R.id.tvOrderId)
    TextView tvOrderId;
    @BindView(R.id.tvOrderAmount)
    TextView tvOrderAmount;
    @BindView(R.id.tvTotalPayment)
    TextView tvTotalPayment;

    @BindView(R.id.etCashAmount)
    EditText etCashAmount;
    @BindView(R.id.etChequeAmount)
    EditText etChequeAmount;
    @BindView(R.id.etChequeNumber)
    EditText etChequeNumber;
    @BindView(R.id.etBankName)
    EditText etBankName;
    @BindView(R.id.etNetAmount)
    EditText etNetAmount;
    @BindView(R.id.etNetBank)
    EditText etNetBank;
    @BindView(R.id.etNetTransactionId)
    EditText etNetTransactionId;

    @BindView(R.id.spTxType)
    Spinner spTxType;

    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;

    @BindView(R.id.recyclerChequeImage)
    RecyclerView recyclerView;

    private CollectionRecyclerAdapter adapter;
    private ArrayList<Cheque> chequeArrayList;
    private int REQUEST_CODE_ORDER_PAYMENT = 2;

    private String invoiceId, retailerName, retailerId;
    private Uri imageUri, imageUriMain = null;
    private int currentPosition;
    private boolean adapterImage;
    private boolean isRetailerCollection = false;
    private float collectionAmount = 0f;
    private double totalCollectedAmount;
    private Invoice invoice;
    private JsonObject object = new JsonObject();
    private String distributorId;
    private String dsrId;

    EditText etOtherCommnets;

    private String[] typeList = {"NEFT", "IMPS", "RTGS"};
    private String type;
    private ArrayAdapter typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_collection);
        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        retailerName = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_NAME);
        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DISTRIBUTOR_ID);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);

        getSupportActionBar().setTitle(retailerName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etOtherCommnets=(EditText)findViewById(R.id.etOtherCommnets);

        try {
            Intent intent = getIntent();
//            isRetailerCollection = intent.getBooleanExtra(Constants.RETAILER_COLLECTION, false);
//            if (isRetailerCollection) {
//                collectionAmount = intent.getFloatExtra(Constants.COLLECTION_AMOUNT, 0);
//            } else {
            invoiceId = intent.getStringExtra(Constants.INVOICE_ID);
            invoice = Invoice.find(Invoice.class, "invoice_id = ? ", new String[]{invoiceId}).get(0);
            try {
                collectionAmount = Float.parseFloat(invoice.getTotalAmount()) - Float.parseFloat(invoice.getCollectedAmount());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            }

            tvOrderId.setText(isRetailerCollection ? "Retailer : " + retailerId : "Invoice : " + invoiceId);
            tvOrderAmount.setText(getString(R.string.text_collection_amount) + UIUtil.amountFormat(String.valueOf(collectionAmount)));


        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chequeArrayList = new ArrayList<>();
        adapter = new CollectionRecyclerAdapter(this, chequeArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new CollectionRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onCamera(View view, int position) {
                currentPosition = position;
                adapterImage = true;
                captureImage();
            }

            @Override
            public void onImagePreview(View view, int position) {
                ImageUtil.previewImage(CollectionActivity.this, chequeArrayList.get(position).getImageUrl());
            }

            @Override
            public void onDelete(View view, int position) {
                deleteConfirm(position);
            }

            @Override
            public void updateCheque(int position, Cheque cheque) {
                chequeArrayList.set(position, cheque);
            }
        });

        typeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTxType.setAdapter(typeAdapter);
        spTxType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = typeList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @OnClick(R.id.ivCamera)
    public void takePhoto() {
        adapterImage = false;
        captureImage();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.imgPreview)
    public void previewImage() {
        ImageUtil.previewImage(this, imageUriMain.getPath());
    }


    @OnClick(R.id.imgPreview)
    public void takeImage() {

    }

    @OnClick(R.id.btnSave)
    public void savePayment() {
        if (validatePayment()) {

            UIUtil.startProgressDialog(this, "Uploading collection");

            object.addProperty("invoice_id", invoiceId);
            object.addProperty("retailer_id", retailerId);
            object.addProperty("distributor_id", distributorId);
            object.addProperty("dsr_id", dsrId);
//            object.addProperty("latitude", location.getLatitude());
//            object.addProperty("longitude", location.getLongitude());
            object.addProperty("other_comments", etOtherCommnets.getText().toString().trim());

            Log.e("object", "O ---------- " + object.toString());


            Call<JsonObject> call = RetrofitAPI.getInstance().getApi().uploadCollection(object,
                    PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE), PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    UIUtil.stopProgressDialog(getApplicationContext());
                    try {
                        JsonObject object = response.body();
                        Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                        if (object.get("status").getAsInt() == 1) {
//                            saveOrder(Constants.ORDER_OPEN);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    UIUtil.stopProgressDialog(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

        }

    }

    private boolean validatePayment() {

        if (TextUtils.isEmpty(etCashAmount.getText().toString()) && TextUtils.isEmpty(etChequeAmount.getText().toString())
                && TextUtils.isEmpty(UIUtil.getText(etNetAmount))) {
            Toast.makeText(this, "Please enter some amount.", Toast.LENGTH_SHORT).show();
            return false;
        }
        object = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < chequeArrayList.size(); i++) {

            Cheque cheque = chequeArrayList.get(i);
            if (TextUtils.isEmpty(cheque.getAmount()) || TextUtils.isEmpty(cheque.getImageUrl())) {
                Toast.makeText(this, "Please enter check details.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                JsonObject obj = new JsonObject();
                obj.addProperty("cheque_amount", cheque.getAmount());
                obj.addProperty("cheque_bank", cheque.getBank());
                obj.addProperty("cheque_number", cheque.getNumber());
                obj.addProperty("cheque_image", cheque.getImageUrl());
                jsonArray.add(obj);
            }
        }

        if (!TextUtils.isEmpty(etChequeAmount.getText().toString())) {
            if (imageUriMain == null) {
                Toast.makeText(this, "Error!  Please add check Image!", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                JsonObject obj = new JsonObject();
                obj.addProperty("cheque_amount", etChequeAmount.getText().toString());
                obj.addProperty("cheque_bank", etBankName.getText().toString());
                obj.addProperty("cheque_number", etChequeNumber.getText().toString());
                obj.addProperty("cheque_image", imageUriMain.getPath());
                jsonArray.add(obj);
            }
        }
        object.add("cheque_details", jsonArray);

        try {

            if (!TextUtils.isEmpty(UIUtil.getText(etNetAmount))) {
                JsonArray array = new JsonArray();
                JsonObject obj = new JsonObject();
                obj.addProperty("transaction_amount", UIUtil.getText(etNetAmount));
                if (TextUtils.isEmpty(UIUtil.getText(etNetTransactionId))) {
                    Toast.makeText(this, "Error!  Enter transaction Id", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    obj.addProperty("transaction_id", UIUtil.getText(etNetTransactionId));
                }
                if (TextUtils.isEmpty(UIUtil.getText(etNetBank))) {
                    Toast.makeText(this, "Error!  Enter transaction Id", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    obj.addProperty("bank_name", UIUtil.getText(etNetBank));
                }
                obj.addProperty("transaction_type", type);
                array.add(obj);
                object.add("net_bank_details", array);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            float orderAmt = collectionAmount;
            String mode = "";

            totalCollectedAmount = 0d;
            if (!TextUtils.isEmpty(etCashAmount.getText().toString())) {
                totalCollectedAmount += Double.parseDouble(etCashAmount.getText().toString());
                object.addProperty("cash_amount", etCashAmount.getText().toString());
            }

            if (!TextUtils.isEmpty(UIUtil.getText(etNetAmount))) {
                totalCollectedAmount += Double.parseDouble(UIUtil.getText(etNetAmount));
            }

            if (!TextUtils.isEmpty(etChequeAmount.getText().toString()))
                totalCollectedAmount += Double.parseDouble(etChequeAmount.getText().toString());

            if (!TextUtils.isEmpty(etCashAmount.getText().toString()) && !TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                object.addProperty("payment_mode", "CASH+CHEQUE");
            } else if (TextUtils.isEmpty(etCashAmount.getText().toString()) && !TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                object.addProperty("payment_mode", "CHEQUE");
            } else if (!TextUtils.isEmpty(etCashAmount.getText().toString()) && TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                object.addProperty("payment_mode", "CASH");
            } else {
                object.addProperty("payment_mode", "CASH");
            }

            for (int i = 0; i < chequeArrayList.size(); i++) {
                try {
                    totalCollectedAmount += Double.parseDouble(chequeArrayList.get(i).getAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            object.addProperty("total_collected_amount", totalCollectedAmount);

            if (orderAmt < totalCollectedAmount) {
                Toast.makeText(this, "Error!  Enter amount is grater than oder amount!", Toast.LENGTH_SHORT).show();
                return false;
            }


            /*"{
       ""cash_amount"": ""10"",
       ""retailer_id"": ""410067"",
       ""invoice_id"": ""sf00001"",
       ""dsr_id"": ""500003"",
       ""payment_mode"": ""CASH/CHEQUE/CASH+CHEQUE"",
       ""total_cheque_amount"": 60.0,
       ""total_collected_amount"": 70.0,
       ""cheque_details"": [{
               ""cheque_amount"": ""20"",
               ""cheque_bank"": ""HDFC"",
               ""cheque_number"": ""454288"",
               ""cheque_image"": ""/storage/emulated/0/SFA PONY/Cheque/IMG_20170711_115644.jpg""
       }, {
               ""cheque_amount"": ""30"",
               ""cheque_bank"": ""sbi"",
               ""cheque_number"": ""8566"",
               ""cheque_image"": ""/storage/emulated/0/SFA PONY/Cheque/IMG_20170711_115622.jpg""
       }]
}"*/

        } catch (Exception e) {
            Toast.makeText(this, "Error!  Please check enter amount", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @OnClick(R.id.btnAdd)
    public void addCheque() {
//        if (TextUtils.isEmpty(tvOrderAmount.getText())){
//            Toast.makeText(this, "Please ch")
//        }
        chequeArrayList.add(new Cheque());
        adapter.notifyDataSetChanged();
    }


    private void deleteConfirm(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Do you want Delete?");

        // Setting Dialog Message
        //alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                chequeArrayList.remove(position);
                adapter.notifyDataSetChanged();
//                setListViewHeightBasedOnItems(chequeRecycleView, chequeArrayList.size(), chequeAdapter);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = ImageUtil.getImagePathURI(this, "Cheque");
        Log.e("imageUri", "imageUri -------- " + imageUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                if (adapterImage) {
                    Cheque cheque = chequeArrayList.get(currentPosition);
                    cheque.setImageUrl(imageUri.getPath());
                    chequeArrayList.set(currentPosition, cheque);
                    adapter.notifyDataSetChanged();
                } else {
                    imageUriMain = imageUri;
                    imgPreview.setImageURI(imageUriMain);
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
