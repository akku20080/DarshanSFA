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
import com.darshansfa.Adapters.CollectionRecyclerAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.ImageUtil;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Cheque;
import com.darshansfa.dbModel.ChequeDetail;

public class OrderCollectionActivity extends AppCompatActivity {

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

    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;

    @BindView(R.id.recyclerChequeImage)
    RecyclerView recyclerView;


    @BindView(R.id.spTxType)
    Spinner spTxType;


    private CollectionRecyclerAdapter adapter;
    private ArrayList<Cheque> chequeArrayList;
    private int REQUEST_CODE_ORDER_PAYMENT = 2;


    private String orderNumber, orderAmount;
    private Uri imageUri, imageUriMain = null;
    private int currentPosition;
    private boolean adapterImage;
    private double totalCollectedAmount;
    private JsonObject object = new JsonObject();
    private ArrayAdapter typeAdapter;

    private String[] typeList = {"NEFT", "IMPS", "RTGS"};
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_collection);
        ButterKnife.bind(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getSupportActionBar().setTitle("Order Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            Intent intent = getIntent();
            orderAmount = intent.getStringExtra("order_amount");
            orderNumber = intent.getStringExtra("order_id");
            tvOrderId.setText("Order Id : " + orderNumber);
            tvOrderAmount.setText(getString(R.string.text_order_amount) + UIUtil.amountFormat(orderAmount));
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
                ImageUtil.previewImage(OrderCollectionActivity.this, chequeArrayList.get(position).getImageUrl());
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
            Intent intent = new Intent();
            Log.e("object", "Collection :   " + object.toString());
            intent.putExtra("collection", object.toString());
//            intent.putExtra(Constants.ADV_COLLECTION, advancePayment);
            setResult(REQUEST_CODE_ORDER_PAYMENT, intent);
            finish();
        }

    }

    private boolean validatePayment() {
        boolean valid = true;

        if (TextUtils.isEmpty(etCashAmount.getText().toString()) && TextUtils.isEmpty(etChequeAmount.getText().toString())
                && TextUtils.isEmpty(etNetAmount.getText().toString())) {
            Toast.makeText(this, "Please enter some amount.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        ArrayList<ChequeDetail> list = new ArrayList<>();
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < chequeArrayList.size(); i++) {


            Cheque cheque = chequeArrayList.get(i);
            if (TextUtils.isEmpty(cheque.getAmount()) || TextUtils.isEmpty(cheque.getImageUrl())) {
                Toast.makeText(this, "Please enter check details.", Toast.LENGTH_SHORT).show();
                valid = false;
            } else {
                JsonObject obj = new JsonObject();
                ChequeDetail detail = new ChequeDetail();
                obj.addProperty("cheque_amount", cheque.getAmount());
                detail.setChequeAmount(cheque.getAmount());
                obj.addProperty("cheque_bank", cheque.getBank());
                detail.setChequeBank(cheque.getBank());
                obj.addProperty("cheque_number", cheque.getNumber());
                detail.setChequeNumber(cheque.getNumber());
                obj.addProperty("cheque_image", cheque.getImageUrl());
                detail.setChequeImage(cheque.getImageUrl());
                jsonArray.add(obj);
                list.add(detail);
            }
        }

        if (!TextUtils.isEmpty(etChequeAmount.getText().toString())) {
            if (imageUriMain == null) {
                Toast.makeText(this, "Error!  Please add check Image!", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                JsonObject obj = new JsonObject();
                ChequeDetail detail = new ChequeDetail();
                obj.addProperty("cheque_amount", etChequeAmount.getText().toString());
                detail.setChequeAmount(UIUtil.getText(etChequeAmount));
                obj.addProperty("cheque_bank", etBankName.getText().toString());
                detail.setChequeBank(UIUtil.getText(etBankName));
                obj.addProperty("cheque_number", etChequeNumber.getText().toString());
                detail.setChequeNumber(UIUtil.getText(etChequeNumber));
                obj.addProperty("cheque_image", imageUriMain.getPath());
                detail.setChequeImage(UIUtil.getText(imageUriMain.getPath()));
                jsonArray.add(obj);
                list.add(detail);
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

            Double orderAmt = Double.parseDouble(orderAmount);

            totalCollectedAmount = 0d;
            if (!TextUtils.isEmpty(etCashAmount.getText().toString())) {
                totalCollectedAmount += Double.parseDouble(etCashAmount.getText().toString());
                object.addProperty("cash_amount", etCashAmount.getText().toString());
            }

            if (!TextUtils.isEmpty(UIUtil.getText(etNetAmount))) {
                totalCollectedAmount += Double.parseDouble(UIUtil.getText(etNetAmount));

            }

            if (!TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                totalCollectedAmount += Double.parseDouble(etChequeAmount.getText().toString());
            }


            if (!TextUtils.isEmpty(etCashAmount.getText().toString()) && !TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                object.addProperty("payment_mode", "CASH+CHEQUE");
            } else if (TextUtils.isEmpty(etCashAmount.getText().toString()) && !TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                object.addProperty("payment_mode", "CHEQUE");
            } else if (!TextUtils.isEmpty(etCashAmount.getText().toString()) && TextUtils.isEmpty(etChequeAmount.getText().toString())) {
                object.addProperty("payment_mode", "CASH");
            } else {
                object.addProperty("payment_mode", "CASH");
            }

            double f = 0f;
            if (!TextUtils.isEmpty(UIUtil.getText(etChequeAmount)))
                f = Double.parseDouble(etChequeAmount.getText().toString());
            for (int i = 0; i < chequeArrayList.size(); i++) {
                try {
                    f += Double.parseDouble(chequeArrayList.get(i).getAmount());
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


    /*"net_bank_details": [{
"transaction_amount": "2500.25",
"bank_name": "HDFC",
"transaction_type": "NEFT/IMPS/RTGS",
"transaction_id": "TxID:123456789"
}]*/


}
