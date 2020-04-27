package com.darshansfa.Activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Models.InvoiceIdResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.ImageUploadUtil;
import com.darshansfa.Utility.ImageUtil;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Invoice;
import com.darshansfa.dbModel.Product;
import com.darshansfa.dbModel.SalesReturn;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SalesReturnActivity extends RuntimePermissionsActivity {

    private static final int REQUEST_PERMISSIONS = 222;
    private static final int IMG_1 = 1, IMG_2 = 2, IMG_3 = 3;

    @BindView(R.id.edPartNumber)
    EditText edPartNumber;
    @BindView(R.id.edPartQty)
    EditText edPartQty;
    @BindView(R.id.edExcessQty)
    EditText edExcessQty;
    @BindView(R.id.edActualPart)
    EditText edActualPart;

    @BindView(R.id.edReasonDescription)
    EditText edReasonDescription;

    @BindView(R.id.spReasonSRN)
    Spinner spReasonSRN;
    @BindView(R.id.spInvoiceSRN)
    Spinner spInvoiceSRN;

    @BindView(R.id.imageView1)
    ImageView imageView1;

    @BindView(R.id.imageView2)
    ImageView imageView2;
    @BindView(R.id.imageView3)
    ImageView imageView3;


//    @BindView(R.id.recyclerImage)
//    RecyclerView recyclerView;

    private String[] reasonList = {"Select Reason", "Wrong Part", "Excess Part", "Shortage", "Other"};
    private String reason, invoiceId;

    private ArrayList<String> invoiceList;
    private String[] imageList;
    private ArrayAdapter reasonAdapter;
    private Uri fileUri;
    private String imagePath1, imagePath2, imagePath3;
    private SalesReturn salesReturn;
    private String retailerId;
    private String distributorId;
    private String dsrId;
    private String retailerName;
    private ArrayAdapter invoiceAdapter;
    private ImageUploadUtil util;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return);
        ButterKnife.bind(this);
        SugarContext.init(SalesReturnActivity.this);

        util = new ImageUploadUtil();
        util.init(this);

        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        retailerName = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_NAME);

        SalesReturnActivity.super.requestAppPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, R.string.accept, REQUEST_PERMISSIONS);

        imageList = new String[3];


        reasonAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, reasonList);
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReasonSRN.setAdapter(reasonAdapter);
        spReasonSRN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason = reasonList[position];
                if (position == 1) {
                    edActualPart.setVisibility(View.VISIBLE);
                    edExcessQty.setVisibility(View.GONE);
                } else {
                    edActualPart.setVisibility(View.GONE);
                    edExcessQty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        invoiceList = new ArrayList<>();
        invoiceList.add("Select");
        List<Invoice> list = Invoice.find(Invoice.class, "retailer_id = ? ", new String[]{retailerId});
        for (int i = 0; i < list.size(); i++) {
            invoiceList.add(list.get(i).getInvoiceId());
        }
        invoiceAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, invoiceList);
        invoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInvoiceSRN.setAdapter(invoiceAdapter);
        spInvoiceSRN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                invoiceId = invoiceList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edPartNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s) || s.length() < 8) {
                    return;
                }

                if (Product.find(Product.class, " part_number = ? ", new String[]{s.toString()}).size() > 0) {
                    getInvoiceIds();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @OnClick(R.id.btnSaveSRN)
    protected void submit() {
        if (validateFields()) {
            updateSalesReturn();
            //Toast.makeText(this, "Demo : " + salesReturn.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void updateSalesReturn() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Adding sales return.. ");
        salesReturn.setRetailerId(retailerId);
        salesReturn.setDsrId(dsrId);
        salesReturn.setDistributorId(distributorId);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String str = gson.toJson(salesReturn);
        JsonObject jsonObject = gson.fromJson(str, JsonObject.class);

        jsonObject.remove("image_url");

        JsonArray array = new JsonArray();
        for (int i = 0; i < imageList.length; i++) {
            array.add(imageList[i]);
        }
        jsonObject.add("img_url", array);

        jsonObject.remove("date");
        jsonObject.remove("id");

        jsonObject.addProperty("invoice_id", invoiceId);
        jsonObject.addProperty("part_number", salesReturn.getProductNumber());
        jsonObject.addProperty("part_quantity", salesReturn.getProductQuantity());
        jsonObject.addProperty("actual_product", salesReturn.getActualProduct());
        Log.e("JsonObject", "JsonObject : " + jsonObject.toString());

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().postSalesReturn(jsonObject, distributorId, dsrId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT);
                    if (1 == object.get("status").getAsInt()) {
                        finish();
                    }
//                    if ()
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Sale return not updated.. Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Sale return not updated..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.imageView1)
    protected void takeImage1() {
        takeImage(IMG_1);
    }

    @OnClick(R.id.imageView2)
    protected void takeImage2() {
        takeImage(IMG_2);
    }

    @OnClick(R.id.imageView3)
    protected void takeImage3() {
        takeImage(IMG_3);
    }

    protected void takeImage(int i) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = ImageUtil.getImagePathURI(this, "retailers");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, i);
    }

    @OnClick(R.id.ivDelete1)
    protected void deleteImage1() {
        imageView1.setImageResource(R.drawable.camera_plus);
        imagePath1 = "";
        imageList[0] = "";
    }

    @OnClick(R.id.ivDelete2)
    protected void deleteImage2() {
        imageView2.setImageResource(R.drawable.camera_plus);
        imagePath2 = "";
        imageList[1] = "";
    }

    @OnClick(R.id.ivDelete3)
    protected void deleteImage3() {
        imageView3.setImageResource(R.drawable.camera_plus);
        imagePath3 = "";
        imageList[2] = "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (salesReturn == null) ;
            salesReturn = new SalesReturn();
            switch (requestCode) {
                case IMG_1:
                    imagePath1 = fileUri.getPath();
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(imageView1);
                    imageList[0] = util.singleFileUpload(imagePath1, "Sales-Return");
                    break;
                case IMG_2:
                    imagePath2 = fileUri.getPath();
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(imageView2);
                    imageList[1] = util.singleFileUpload(imagePath2, "Sales-Return");
                    break;
                case IMG_3:
                    imagePath3 = fileUri.getPath();
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(imageView3);
                    imageList[2] = util.singleFileUpload(imagePath3, "Sales-Return");
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Failed to capture camera  image", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields() {
        boolean valid = true;
        if (salesReturn == null) ;
        salesReturn = new SalesReturn();
        salesReturn.setActualProduct("");


//        if (!TextUtils.isEmpty(imagePath1)) {
//            ImageUploadUtil util = new ImageUploadUtil();
//            util.init(this);
//            salesReturn.setImageUrl(util.singleFileUpload(imagePath1, "Sales-Return"));
//        } else {
////            salesReturn.setImageUrl("");
//        }
//        if (!TextUtils.isEmpty(imagePath3)) {
//            ImageUploadUtil util = new ImageUploadUtil();
//            util.init(this);
//            salesReturn.setImageUrl(util.singleFileUpload(imagePath2, "Sales-Return"));
//        } else {
////            salesReturn.setImageUrl("");
//        }
//        if (!TextUtils.isEmpty(imagePath3)) {
//            ImageUploadUtil util = new ImageUploadUtil();
//            util.init(this);
//            salesReturn.setImageUrl(util.singleFileUpload(imagePath3, "Sales-Return"));
//        } else {
////            salesReturn.setImageUrl("");
//        }
        if (TextUtils.isEmpty(UIUtil.getText(edPartNumber))) {
            edPartNumber.setError("Enter product number");
            valid = false;
        } else {
            edPartNumber.setError(null);
            if (Product.find(Product.class, "part_number = ?", new String[]{edPartNumber.getText().toString()}).size() > 0) {
                salesReturn.setProductNumber(edPartNumber.getText().toString());
            } else {
                edPartNumber.setError("Invalid parts ");
                valid = false;
            }
        }
        if (TextUtils.isEmpty(UIUtil.getText(edPartQty))) {
            edPartQty.setError("Enter product quantity");
            valid = false;
        } else {
            salesReturn.setProductQuantity(UIUtil.getText(edPartQty));
            edPartQty.setError(null);
        }
        if ("Wrong Part".equalsIgnoreCase(reason)) {
            if (TextUtils.isEmpty(UIUtil.getText(edActualPart))) {
                edActualPart.setError("Enter Actual product");
                valid = false;
            } else {
                edActualPart.setError(null);
                salesReturn.setActualProduct(UIUtil.getText(edActualPart));
            }
        } else if ("Excess Part".equalsIgnoreCase(reason)) {
            if (TextUtils.isEmpty(UIUtil.getText(edExcessQty))) {
                edExcessQty.setError("Enter quantity ");
                valid = false;
            } else {
                edExcessQty.setError(null);
                salesReturn.setExcessQuantity(UIUtil.getText(edExcessQty));
                salesReturn.setShortageQuantity("0");
            }
        } else {
            if (TextUtils.isEmpty(UIUtil.getText(edExcessQty))) {
                edExcessQty.setError("Enter quantity ");
                valid = false;
            } else {
                edExcessQty.setError(null);
                salesReturn.setShortageQuantity(UIUtil.getText(edExcessQty));
                salesReturn.setExcessQuantity("0");
            }
        }
        if ("Select Reason".equalsIgnoreCase(reason)) {
            valid = false;
            Toast.makeText(this, "Please select reason", Toast.LENGTH_SHORT).show();
        } else {
            salesReturn.setReason(reason);
        }


//        if ("Select".equalsIgnoreCase(invoiceId)) {
//            valid = false;
//            Toast.makeText(this, "Please select invoice id", Toast.LENGTH_SHORT).show();
//        } else {
//            salesReturn.setInvoiceId(invoiceId);
//        }
        salesReturn.setReasonDescription(UIUtil.getText(edReasonDescription));


        return valid;
    }


    private void getInvoiceIds() {

        if (TextUtils.isEmpty(UIUtil.getText(edPartNumber))) {
            return;
        }
        String partNumber = edPartNumber.getText().toString();
        Call<InvoiceIdResponse> call = RetrofitAPI.getInstance().getApi().getInvoiceIdsForSaleReturn(retailerId, partNumber);
        call.enqueue(new Callback<InvoiceIdResponse>() {
            @Override
            public void onResponse(Call<InvoiceIdResponse> call, Response<InvoiceIdResponse> response) {
                try {
                    Log.e("InvoiceIdResponse", "InvoiceIdResponse : " + response.body());
                    invoiceList.clear();
                    invoiceList.add("Select");
                    invoiceList.addAll(response.body().getInvoiceIdList());
                    invoiceAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<InvoiceIdResponse> call, Throwable t) {
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
