package com.darshansfa.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Models.ImageUpload;
import com.darshansfa.Models.RetailerResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.AppLocationProvider;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.ImageUploadUtil;
import com.darshansfa.Utility.ImageUtil;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.NewRetailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRetailerActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final int CAPTURE_SIGN = 21;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int ID_PROOF = 12, SHOP_IMG = 13, RETAILER_IMG = 14,
            AADHAR_IMG = 15, VOTER_IMG = 16, DRIVING_IMG = 17, PAN_IMG = 18, SIGN_IMG = 19;
    private static final int REQUEST_PERMISSIONS = 121;
    private String IMAGE_PATH = "";

    private Uri fileUri;
    private String profilepicUrl;
    // file url to store image/video
    public NewRetailer newRetailer;
    Button Save, btnAddRetailer;
    static boolean captureSig = false;
    ImageView ivIndentiity, ivShopImg, ivRetailerProfile, ivRetailerSignature;
    EditText edFirstName, edMiddleName, edNearDealerName, edEmail, edTotalCounter, edSurname,
            edShopSize, edTotalSale, edShopNameNo, edShopName, edStreetName,
            edTown, edDistrict, edState, edPincode, edAddMobileNumber;
    EditText edCvdealer, edRetailerMobilenumber, edTin, edLandline, edGst;

    private EditText edDriving, edVoterId, edPan, edAadhar;
    private ImageView ivDriving, ivVoterId, ivPan, ivAadhar, ivDOB;
    private String pinCode, locality;
    private Spinner spLocality, spGstType;
    private ArrayList<String> localities;
    private String[] gstTypeList = {"select", "Registered", "Unregistered", "Applied"};
    private ArrayAdapter arrayAdapterSp;
    private TextView tvDOB;
    private String retailerId, gstType,gstType_sel="";
    private ArrayAdapter gstTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_retailer);

        newRetailer = new NewRetailer();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            Intent intent = getIntent();
            if (intent.getBooleanExtra(Constants.UPDATE, false)) {
                retailerId = intent.getStringExtra(Constants.RETAILER_LOGIN);
                setEditable(false);
                getRetailerDetails(retailerId);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        AddRetailerActivity.super.requestAppPermissions(new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, R.string.accept, REQUEST_PERMISSIONS);

        IMAGE_PATH = PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.DISTRIBUTOR_ID) + "/"
                + PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.DSR_ID) + "/retailer-profile";

        localities = new ArrayList<>();
        spLocality = (Spinner) findViewById(R.id.locality);
        arrayAdapterSp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, localities);
        arrayAdapterSp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocality.setAdapter(arrayAdapterSp);

        spGstType = (Spinner) findViewById(R.id.spGstType);
        gstTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gstTypeList);
        gstTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGstType.setAdapter(gstTypeAdapter);
        spGstType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gstType = gstTypeList[position];
                if(position==1){
                    gstType_sel = "REG";
                }else if(position==2){
                    gstType_sel = "UNREG";
                }else if(position==3){
                    gstType_sel = "APPLIED";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edEmail = (EditText) findViewById(R.id.edEmail);
        edFirstName = (EditText) findViewById(R.id.ed_retailer_fName);
        edSurname = (EditText) findViewById(R.id.ed_retailer_mLast);
        edShopSize = (EditText) findViewById(R.id.ed_shopesize);
        edStreetName = (EditText) findViewById(R.id.ed_streetname);
        edTown = (EditText) findViewById(R.id.ed_town);
        edDistrict = (EditText) findViewById(R.id.ed_district);
        edState = (EditText) findViewById(R.id.ed_state);
        edPincode = (EditText) findViewById(R.id.ed_pincode);
        edShopNameNo = (EditText) findViewById(R.id.ed_shopname);
        edShopName = (EditText) findViewById(R.id.edNameShop);
        edCvdealer = (EditText) findViewById(R.id.edNearDealerName);
        edRetailerMobilenumber = (EditText) findViewById(R.id.ed_retailer_mobilenumber);
        edNearDealerName = (EditText) findViewById(R.id.edNearDealerName);

        edDriving = (EditText) findViewById(R.id.edDriving);
        edVoterId = (EditText) findViewById(R.id.edVoterId);
        edPan = (EditText) findViewById(R.id.edPan);
        edTotalSale = (EditText) findViewById(R.id.edTotalSale);
        edAadhar = (EditText) findViewById(R.id.edAadhar);
        edAddMobileNumber = (EditText) findViewById(R.id.edAddMobileNumber);
        edTotalCounter = (EditText) findViewById(R.id.edTotalCounter);
        edTin = (EditText) findViewById(R.id.edTin);
        edLandline = (EditText) findViewById(R.id.edLandline);
        edGst = (EditText) findViewById(R.id.edGst);

        ivIndentiity = (ImageView) findViewById(R.id.iv_identity_camera);
        ivShopImg = (ImageView) findViewById(R.id.iv_camera_shop);
        ivRetailerProfile = (ImageView) findViewById(R.id.iv_retailer_image);
        ivRetailerSignature = (ImageView) findViewById(R.id.iv_retailer_sig_capture);
        ivAadhar = (ImageView) findViewById(R.id.ivAadhar);
        ivVoterId = (ImageView) findViewById(R.id.ivVoterId);
        ivPan = (ImageView) findViewById(R.id.ivPan);
        ivDriving = (ImageView) findViewById(R.id.ivDriving);
        ivDOB = (ImageView) findViewById(R.id.ivDOB);
        tvDOB = (TextView) findViewById(R.id.tvDOB);


        ivShopImg.setOnClickListener(this);
        ivRetailerSignature.setOnClickListener(this);
        ivRetailerProfile.setOnClickListener(this);

        ivAadhar.setOnClickListener(this);
        ivVoterId.setOnClickListener(this);
        ivDriving.setOnClickListener(this);
        ivPan.setOnClickListener(this);
        ivDOB.setOnClickListener(this);

        btnAddRetailer = (Button) findViewById(R.id.btnAddRetailer);
        btnAddRetailer.setOnClickListener(this);

        btnAddRetailer = (Button) findViewById(R.id.btnAddRetailer);
        btnAddRetailer.setVisibility(TextUtils.isEmpty(retailerId) ? View.VISIBLE : View.GONE);

        spLocality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locality = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Item : " + locality, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("Chnahe", "------------------------" + s.toString());
                pinCode = s.toString();
                if (!TextUtils.isEmpty(s) && (s.length() > 5)) {
                    if (Pattern.compile("[0-9]{6}|[0-9]{3}\\s[0-9]{3}").matcher(pinCode).matches()) {
                        edPincode.setError(null);
                    } else {
                        edPincode.setError("Invalid pincode.!");
                    }
                    getLocalities();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


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

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getLocalities() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Getting locality, Please wait");


        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().localityFromPincode(pinCode, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject jsonObject = response.body();
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());

                    edState.setText(jsonObject.get("state").getAsString());
                    edTown.setText(jsonObject.get("city").getAsString());
                    edDistrict.setText(jsonObject.get("district").getAsString());
                    JsonArray jsonArray = jsonObject.getAsJsonArray("locality");
                    localities.clear();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        localities.add(jsonArray.get(i).getAsJsonObject().get("locality_name").getAsString());
                        //localities.add(jsonArray.get(i).getAsJsonObject().get("locality_name").getAsString());
                        //localities.add(jsonArray.get(i).getAsJsonObject().get("locality_name").getAsString());
                        //localities.add(jsonArray.get(i).getAsJsonObject().get("locality_name").getAsString());
                        //localities.add(jsonArray.get(i).getAsJsonObject().get("locality_name").getAsString());
                        //localities.add(jsonArray.get(i).getAsJsonObject().get("locality_name").getAsString());
                    }
                    arrayAdapterSp.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_retailer_sig_capture:
                Intent myIntent = new Intent(getApplicationContext(), CaptureSignature.class);
                startActivityForResult(myIntent, CAPTURE_SIGN);
                break;
            case R.id.btnAddRetailer:
                if (isFieldValid()) {
                    createJSON();
                }
                break;
            case R.id.iv_identity_camera:
                captureImage(ID_PROOF);
                break;
            case R.id.iv_camera_shop:
                captureImage(SHOP_IMG);
                break;
            case R.id.iv_retailer_image:
                captureImage(RETAILER_IMG);
                break;
            case R.id.ivAadhar:
                captureImage(AADHAR_IMG);
                break;
            case R.id.ivVoterId:
                captureImage(VOTER_IMG);
                break;
            case R.id.ivPan:
                captureImage(PAN_IMG);
                break;
            case R.id.ivDriving:
                captureImage(DRIVING_IMG);
                break;
            case R.id.ivDOB:
                openDatePicker();
                break;


            default:
                break;
        }

    }


    public void addRetailer() {
        Log.e("addRetailer", "DDDDDDD_D_D_D_D_D_D_D_DD_D_D_D_");
        UIUtil.stopProgressDialog(this);

        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Adding new retailer, Please wait..");

        Gson gson = new Gson();
        JsonObject object = gson.fromJson(gson.toJson(newRetailer), JsonObject.class);
        Log.e("object", "object ---------------------- : " + object);

        Call<JsonObject> call;
//        if (TextUtils.isEmpty(retailerId)) {
        call = RetrofitAPI.getInstance().getApi().addRetailer(object, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));
//        } else {
//            call = RetrofitAPI.getInstance().getApi().updateRetailer(object, PreferencesManger.getStringFields(this, Constants.Pref.DISTRIBUTOR_ID),
//                    PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID), retailerId);
//        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject jsonObject = response.body();
                    Toast.makeText(AddRetailerActivity.this, jsonObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (jsonObject.get("status").getAsInt() == 1) {
                        finish();
                    }
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());

                } catch (Exception e) {
                    Toast.makeText(AddRetailerActivity.this, "Something went wrong, Server not responding, Check internet Connection!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(AddRetailerActivity.this, "Something went wrong, Server not responding, Check internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public boolean isFieldValid() {
        boolean valid = true;

//        if (TextUtils.isEmpty(edIdentity.getText().toString())) {
//            edIdentity.setError("Adhar card number is required");
//            valid = false;
//        } else {
//            edIdentity.setError(null);
//        }


       /* if (TextUtils.isEmpty(edTotalCounter.getText().toString())) {
            edTotalCounter.setError("This field is required");
            edTotalCounter.requestFocus();
            valid = false;
        } else {
            edTotalCounter.setError(null);
        }


        if (TextUtils.isEmpty(edTotalSale.getText().toString())) {
            edTotalSale.setError("This field is required");
            edTotalSale.requestFocus();
            valid = false;
        } else {
            edTotalSale.setError(null);
        }
*/

        if (TextUtils.isEmpty(edPincode.getText().toString())) {
            edPincode.setError("This field is required");
            valid = false;
        } else {
            edPincode.setError(null);
        }
        if (TextUtils.isEmpty(edState.getText().toString())) {
            edState.setError("This field is required");
            edState.requestFocus();
            valid = false;
        } else {
            edState.setError(null);
        }

//        if (TextUtils.isEmpty(edDistrict.getText().toString())) {
//            edDistrict.setError("This field is required");
//            edDistrict.requestFocus();
//            valid = false;
//        } else {
//            edDistrict.setError(null);
//        }

        if (TextUtils.isEmpty(edTown.getText().toString())) {
            edTown.setError("This field is required");
            edTown.requestFocus();
            valid = false;
        } else {
            edTown.setError(null);
        }
//        if (TextUtils.isEmpty(edTaluka.getText().toString())) {
//            edTaluka.setError("This field is required");
//            edTaluka.requestFocus();
//            valid = false;
//        } else {
//            edTaluka.setError(null);
//        }
//        if (TextUtils.isEmpty(edTehsilName.getText().toString())) {
//            edTehsilName.setError("This field is required");
//            edTehsilName.requestFocus();
//            valid = false;
//        } else {
//            edTehsilName.setError(null);
//        }

//        if (TextUtils.isEmpty(edLocality.getText().toString())) {
//            edLocality.setError("This field is required");
//            edStreetName.requestFocus();
//            valid = false;
//        } else {
//            edLocality.setError(null);
//        }

        if (TextUtils.isEmpty(edStreetName.getText().toString())) {
            edStreetName.setError("This field is required");
            edStreetName.requestFocus();
            valid = false;
        } else {
            edStreetName.setError(null);
        }

        if (TextUtils.isEmpty(edShopNameNo.getText().toString())) {
            edShopNameNo.setError("This field is required");
            edShopNameNo.requestFocus();
            valid = false;
        } else {
            edShopNameNo.setError(null);
        }


        if (TextUtils.isEmpty(edRetailerMobilenumber.getText().toString())) {
            edRetailerMobilenumber.setError("This field is required");
            edRetailerMobilenumber.requestFocus();
            valid = false;
        } else if (edRetailerMobilenumber.getText().toString().length() != 10) {
            edRetailerMobilenumber.setError("Invalid Mobile number.!  Enter only 10 digit mobile number");
            valid = false;
        } else {
            edRetailerMobilenumber.setError(null);
        }


        if (TextUtils.isEmpty(edFirstName.getText().toString())) {
            edFirstName.setError("This field is required");
            edFirstName.requestFocus();
            valid = false;
        } else {
            edFirstName.setError(null);
        }

/*
    if (TextUtils.isEmpty(edTin.getText().toString())) {
            edTin.setError("This field is required");
            edTin.requestFocus();
            valid = false;
        } else {
            edTin.setError(null);
        }
 */


        if (TextUtils.isEmpty(edShopName.getText().toString())) {
            edShopName.setError("This field is required");
            edShopName.requestFocus();
            valid = false;
        } else {
            edShopName.setError(null);
        }

        if (TextUtils.isEmpty(tvDOB.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please select Date of birth!", Toast.LENGTH_SHORT).show();
            valid = false;
        }


//        if (TextUtils.isEmpty(idProofImgPath)) {
//            Toast.makeText(getApplicationContext(), "please add ID proof Image", Toast.LENGTH_SHORT).show();
//            valid = false;
//        }
//        if (TextUtils.isEmpty(retailerImgPath)) {
//            Toast.makeText(getApplicationContext(), "please add Retailer Image", Toast.LENGTH_SHORT).show();
//            valid = false;
//        }
//        if (TextUtils.isEmpty(shopImgPath)) {
//            Toast.makeText(getApplicationContext(), "please add Shop Image", Toast.LENGTH_SHORT).show();
//            valid = false;
//        }

        if (TextUtils.isEmpty(newRetailer.getSignatureUrl())) {
            Toast.makeText(getApplicationContext(), "please take Retailer signature", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if ("select".equalsIgnoreCase(gstType)) {
            Toast.makeText(getApplicationContext(), "Please select GST Type ", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if ("Registered".equalsIgnoreCase(gstType)) {
            edGst.setError("GST number required.");
            valid = false;
        } else {
            edGst.setError(null);
        }


        return valid;
    }

    private void getCurrentLocation() {
        Log.e("place", "Ordre ------------------ place");
        UIUtil.startProgressDialog(this, "Getting location, Please wait..");
        AppLocationProvider.requestForLocation(this, new AppLocationProvider.LocationCallback() {
            @Override
            public void onLocationAvailable(Location loc) {
                newRetailer.setLatitude(String.valueOf(loc.getLatitude()));
                newRetailer.setLongitude(String.valueOf(loc.getLongitude()));
                Log.e("place", "Ordre --" + "Lat : " + loc.getLatitude() + ", Long : " + loc.getLongitude());
                UIUtil.stopProgressDialog(AddRetailerActivity.this);
                uploadImages();
                Toast.makeText(getApplicationContext(), "Lat : " + loc.getLatitude() + ", Long : " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createJSON() {

        try {

            newRetailer.setDsrId(PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.DSR_ID));
            newRetailer.setDistributorId(PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.DISTRIBUTOR_ID));

            newRetailer.setShopName(edShopName.getText().toString());
            newRetailer.setFirstName(edFirstName.getText().toString());
            newRetailer.setLastName(edSurname.getText().toString());
            newRetailer.setEmail(edEmail.getText().toString());
            newRetailer.setMobile(edRetailerMobilenumber.getText().toString());
            newRetailer.setMobile2(edAddMobileNumber.getText().toString());
            newRetailer.setShopSize(edShopSize.getText().toString());
            newRetailer.setShopNumber(edShopNameNo.getText().toString());
            newRetailer.setDateOfBirth(tvDOB.getText().toString());
            newRetailer.setLandline(edLandline.getText().toString());


            //Address
            newRetailer.setStreet(edStreetName.getText().toString());
            newRetailer.setLocality(locality);
            newRetailer.setCity(edTown.getText().toString());
            newRetailer.setDistrict(edDistrict.getText().toString());
            newRetailer.setState(edState.getText().toString());
            newRetailer.setPincode(edPincode.getText().toString());


            newRetailer.setNearDealerName(edNearDealerName.getText().toString());
            newRetailer.setTotalSale(edTotalSale.getText().toString());
            newRetailer.setTotalCounterSale(edTotalCounter.getText().toString());
            newRetailer.setGst(edGst.getText().toString());
            newRetailer.setTINNumber(edTin.getText().toString());


            newRetailer.setVoterIdNumber(edVoterId.getText().toString());
            newRetailer.setPanCardNumber(edPan.getText().toString());
            newRetailer.setAadharCardNumber(edAadhar.getText().toString());
            newRetailer.setDrivingLicenceImage(edDriving.getText().toString());
            newRetailer.setGst(UIUtil.getText(edGst));

            newRetailer.setGstStatus(gstType_sel);


            getCurrentLocation();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Please check details once!!", Toast.LENGTH_SHORT).show();
            Log.e("JSONException", "Error while createing Json");
        }
    }


    private void imagePreview(String getUrl) {


        File imgFile1 = new File(getUrl);
        newRetailer.setSignatureUrl(getUrl);
        if (imgFile1.exists()) {
            ivRetailerSignature.setVisibility(View.VISIBLE);
            newRetailer.setSignatureUrl(imgFile1.getAbsolutePath());
//            Picasso.with(this).load(imgFile1).into(ivRetailerSignature);
            Log.e("sign", imgFile1.getAbsolutePath());
            Glide.with(this).load(imgFile1).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(ivRetailerSignature);
            captureSig = false;
        }


    }


    public void captureImage(int code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = ImageUtil.getImagePathURI(this, "retailers");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, code);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode != -1 && (requestCode & 0xffff0000) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        }
        super.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image

        try {
            if (data != null) {
                if (requestCode == CAPTURE_SIGN) {
                    data.getStringExtra("Signature");
                    imagePreview(data.getStringExtra("Signature"));
                    return;
                }
            }/*else {
            // failed to capture image
            Toast.makeText(getApplicationContext(),
                    "Sorry! Failed to capture Signature image", Toast.LENGTH_SHORT)
                    .show();
        }*/

            if (resultCode == RESULT_OK) {
                // display it in image view
                previewCapturedImage(requestCode);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture camera  image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Display image from a path to ImageView
     */
    public void previewCapturedImage(int code) {

        try {

            ivRetailerProfile.setVisibility(View.VISIBLE);
            profilepicUrl = fileUri.getPath();
//            ImageUploadUtil util = new ImageUploadUtil();
//            util.init(getApplicationContext());
//            String s = util.singleFileUpload(profilepicUrl, "demo");
//            Log.e("Img", "Img ------------------------- : " + s);
//            finish();


            switch (code) {
                case ID_PROOF:
                    newRetailer.setIdentityUrl(profilepicUrl);
//                    Picasso.with(this).load(fileUri).resize(50, 50).centerCrop().into(ivIndentiity);
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(ivIndentiity);
                    ivIndentiity.requestFocus();
                    break;
                case RETAILER_IMG:
                    newRetailer.setImageUrl(profilepicUrl);
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(ivRetailerProfile);
                    ivRetailerProfile.requestFocus();
                    break;
                case SHOP_IMG:
                    newRetailer.setShopPhoto(profilepicUrl);
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(ivShopImg);
                    ivShopImg.requestFocus();
                    break;
                case AADHAR_IMG:
                    newRetailer.setAadharCardImage(profilepicUrl);
                    Log.e("profilepicUrl", "profilepicUrl ; ==== " + profilepicUrl);
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(ivAadhar);
                    ivAadhar.requestFocus();
                    break;
                case VOTER_IMG:
                    newRetailer.setVoterIdImage(profilepicUrl);
                    Log.e("profilepicUrl", "profilepicUrl ; ==== " + profilepicUrl);
                    Glide.with(this).load(fileUri).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(ivVoterId);
                    ivVoterId.requestFocus();
                    break;
                case PAN_IMG:
                    Log.e("profilepicUrl", "profilepicUrl ; ==== " + profilepicUrl);
                    newRetailer.setPanCardImage(profilepicUrl);
                    Glide.with(this).load(fileUri).thumbnail(0.1f).placeholder(R.drawable.progress_animation).into(ivPan);
                    ivPan.requestFocus();
                    break;
                case DRIVING_IMG:
                    Log.e("profilepicUrl", "profilepicUrl ; ==== " + profilepicUrl);
                    newRetailer.setDrivingLicenceImage(profilepicUrl);
                    Glide.with(this).load(fileUri).placeholder(R.drawable.progress_animation).into(ivDriving);
                    ivDriving.requestFocus();
                    break;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void uploadImages() {
        ImageUploadUtil imageUploadUtil = new ImageUploadUtil();
        imageUploadUtil.init(getApplicationContext());

        Log.e(" Codinition ", "check ------------------- : " + (!TextUtils.isEmpty(newRetailer.getAadharCardImage()) && !newRetailer.getAadharCardImage().contains("https://")));
        if (!TextUtils.isEmpty(newRetailer.getAadharCardImage()) && !newRetailer.getAadharCardImage().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(AADHAR_IMG, newRetailer.getAadharCardImage(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getPanCardImage()) && !newRetailer.getPanCardImage().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(PAN_IMG, newRetailer.getPanCardImage(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getDrivingLicenceImage()) && !newRetailer.getDrivingLicenceImage().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(DRIVING_IMG, newRetailer.getDrivingLicenceImage(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getVoterIdImage()) && !newRetailer.getVoterIdImage().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(VOTER_IMG, newRetailer.getVoterIdImage(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getImageUrl()) && !newRetailer.getImageUrl().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(RETAILER_IMG, newRetailer.getImageUrl(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getShopPhoto()) && !newRetailer.getShopPhoto().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(SHOP_IMG, newRetailer.getShopPhoto(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getIdentityUrl()) && !newRetailer.getIdentityUrl().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(ID_PROOF, newRetailer.getIdentityUrl(), IMAGE_PATH));

        if (!TextUtils.isEmpty(newRetailer.getSignatureUrl()) && !newRetailer.getSignatureUrl().contains("https://"))
            imageUploadUtil.startImageUpload(new ImageUpload(SIGN_IMG, newRetailer.getSignatureUrl(), IMAGE_PATH));

        Log.e("APp", " imageUploadUtil is : " + imageUploadUtil.isUploadingInProgress());
        if (imageUploadUtil.isUploadingInProgress()) {
            UIUtil.startProgressDialog(this, "Uploading Images, Please wait...");
        } else {
            addRetailer();
        }

        imageUploadUtil.SetImageUploadListener(new ImageUploadUtil.ImageUploadListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("onStateChanged", "  id : " + id + " : TransferState : " + state.name());
            }

            @Override
            public void onProgressChanged(ImageUpload imageUpload) {

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("onError", "  id : " + id + " : Exception : " + ex.toString());
            }

            @Override
            public void onUploadComplete(ImageUpload imageUpload) {
                updateImageURL(imageUpload);
                Log.e("onUploadComplete", "  imageUpload : " + imageUpload);
            }

            @Override
            public void allImageUpload() {
                UIUtil.stopProgressDialog(AddRetailerActivity.this);
                addRetailer();
                Log.e("allImageUpload", " ---------------------- allImageUpload");
            }

            @Override
            public void onSingleImageUpload(String imgUrl) {

            }
        });
    }

    private void updateImageURL(ImageUpload imageUpload) {

        Log.e("ImageUpload", "ImageUpload : " + imageUpload);

        switch (imageUpload.getImageType()) {
            case VOTER_IMG:
                newRetailer.setVoterIdImage(imageUpload.getUrl());
                break;
            case AADHAR_IMG:
                newRetailer.setAadharCardImage(imageUpload.getUrl());
                break;
            case PAN_IMG:
                newRetailer.setPanCardImage(imageUpload.getUrl());
                break;
            case DRIVING_IMG:
                newRetailer.setDrivingLicenceImage(imageUpload.getUrl());
                break;
            case SHOP_IMG:
                newRetailer.setShopPhoto(imageUpload.getUrl());
                break;
            case RETAILER_IMG:
                newRetailer.setImageUrl(imageUpload.getUrl());
                break;
            case ID_PROOF:
                newRetailer.setIdentityUrl(imageUpload.getUrl());
                break;
            case SIGN_IMG:
                newRetailer.setSignatureUrl(imageUpload.getUrl());
                break;
        }
    }

    private void openDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                monthOfYear = monthOfYear + 1;

                String month, day;
                //Calendar myCalendar = Calendar.getInstance();
                if (monthOfYear < 10) {
                    month = "0" + String.valueOf(monthOfYear);
                } else {
                    month = String.valueOf(monthOfYear);
                }

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);
                } else {
                    day = String.valueOf(dayOfMonth);
                }

                String date = "" + day + "-" + month + "-" + year;
                tvDOB.setText(date);


            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void bindData() {

        try {
            edShopName.setText(newRetailer.getShopName());
            edFirstName.setText(newRetailer.getFirstName());
            edSurname.setText(newRetailer.getLastName());
            edEmail.setText(newRetailer.getEmail());
            edRetailerMobilenumber.setText(newRetailer.getMobile());
            edAddMobileNumber.setText(newRetailer.getMobile2());
            edShopSize.setText(newRetailer.getShopSize());
            edShopNameNo.setText(newRetailer.getShopNumber());
            tvDOB.setText(newRetailer.getDateOfBirth());
            edLandline.setText(newRetailer.getLandline());

            localities.clear();
            localities.add(newRetailer.getLocality());

            edStreetName.setText(newRetailer.getStreet());
            edTown.setText(newRetailer.getCity());
            edDistrict.setText(newRetailer.getDistrict());
            edState.setText(newRetailer.getState());
            edPincode.setText(newRetailer.getPincode());

            edNearDealerName.setText(newRetailer.getNearDealerName());
            edTotalSale.setText(newRetailer.getTotalSale());
            edTotalCounter.setText(newRetailer.getTotalCounterSale());
            edGst.setText(newRetailer.getGst());
            edTin.setText(newRetailer.getTINNumber());

            edVoterId.setText(newRetailer.getVoterIdNumber());
            edPan.setText(newRetailer.getPanCardNumber());
            edAadhar.setText(newRetailer.getAadharCardNumber());
            edDriving.setText(newRetailer.getDrivingLicenceNumber());

            Log.e("newRetailer", "newRetailer : " + newRetailer.toString());

            Glide.with(this).load(newRetailer.getAadharCardImage()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.camera_plus).into(ivAadhar);
            Glide.with(this).load(newRetailer.getVoterIdImage()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.camera_plus).into(ivVoterId);
            Glide.with(this).load(newRetailer.getPanCardImage()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.camera_plus).into(ivPan);
            Glide.with(this).load(newRetailer.getDrivingLicenceImage()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.camera_plus).into(ivDriving);
            Glide.with(this).load(newRetailer.getImageUrl()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.camera_plus).into(ivRetailerProfile);
            Glide.with(this).load(newRetailer.getRetailerSignImageUrl()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.sig_cap).into(ivRetailerSignature);
            Glide.with(this).load(newRetailer.getShopPhoto()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).error(R.drawable.sig_cap).into(ivShopImg);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getRetailerDetails(String retailerId) {

        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "getting retailer details, Please wait..");


        Call<RetailerResponse> call = RetrofitAPI.getInstance().getApi().getRetailer(PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID), retailerId);

        call.enqueue(new Callback<RetailerResponse>() {
            @Override
            public void onResponse(Call<RetailerResponse> call, Response<RetailerResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    RetailerResponse retailerResponse = response.body();
                    if (retailerResponse.getStatus() == 1) {
                        newRetailer = retailerResponse.getRetailer();
                        bindData();
                    }
                    Toast.makeText(AddRetailerActivity.this, retailerResponse.getMessage(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RetailerResponse> call, Throwable t) {
                t.printStackTrace();
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setEditable(boolean b) {

        edEmail = (EditText) findViewById(R.id.edEmail);
        edFirstName = (EditText) findViewById(R.id.ed_retailer_fName);
        edSurname = (EditText) findViewById(R.id.ed_retailer_mLast);
        edShopSize = (EditText) findViewById(R.id.ed_shopesize);
        edStreetName = (EditText) findViewById(R.id.ed_streetname);
        edTown = (EditText) findViewById(R.id.ed_town);
        edDistrict = (EditText) findViewById(R.id.ed_district);
        edState = (EditText) findViewById(R.id.ed_state);
        edPincode = (EditText) findViewById(R.id.ed_pincode);
        edShopNameNo = (EditText) findViewById(R.id.ed_shopname);
        edShopName = (EditText) findViewById(R.id.edNameShop);
        edCvdealer = (EditText) findViewById(R.id.edNearDealerName);
        edRetailerMobilenumber = (EditText) findViewById(R.id.ed_retailer_mobilenumber);
        edNearDealerName = (EditText) findViewById(R.id.edNearDealerName);

        edDriving = (EditText) findViewById(R.id.edDriving);
        edVoterId = (EditText) findViewById(R.id.edVoterId);
        edPan = (EditText) findViewById(R.id.edPan);
        edTotalSale = (EditText) findViewById(R.id.edTotalSale);
        edAadhar = (EditText) findViewById(R.id.edAadhar);
        edAddMobileNumber = (EditText) findViewById(R.id.edAddMobileNumber);
        edTotalCounter = (EditText) findViewById(R.id.edTotalCounter);
        edTin = (EditText) findViewById(R.id.edTin);
        edLandline = (EditText) findViewById(R.id.edLandline);
        edGst = (EditText) findViewById(R.id.edGst);

        //ivIndentiity = (ImageView) findViewById(R.id.iv_identity_camera);
        ivShopImg = (ImageView) findViewById(R.id.iv_camera_shop);
        ivRetailerProfile = (ImageView) findViewById(R.id.iv_retailer_image);
        ivRetailerSignature = (ImageView) findViewById(R.id.iv_retailer_sig_capture);
        ivAadhar = (ImageView) findViewById(R.id.ivAadhar);
        ivVoterId = (ImageView) findViewById(R.id.ivVoterId);
        ivPan = (ImageView) findViewById(R.id.ivPan);
        ivDriving = (ImageView) findViewById(R.id.ivDriving);
        ivDOB = (ImageView) findViewById(R.id.ivDOB);
        tvDOB = (TextView) findViewById(R.id.tvDOB);


        edEmail.setEnabled(b);
        edFirstName.setEnabled(b);
        edSurname.setEnabled(b);
        edShopSize.setEnabled(b);
        edStreetName.setEnabled(b);
        edTown.setEnabled(b);
        edDistrict.setEnabled(b);
        edState.setEnabled(b);
        edPincode.setEnabled(b);
        edShopNameNo.setEnabled(b);
        edShopName.setEnabled(b);
        edCvdealer.setEnabled(b);
        edRetailerMobilenumber.setEnabled(b);
        edNearDealerName.setEnabled(b);

        edDriving.setEnabled(b);
        edVoterId.setEnabled(b);
        edPan.setEnabled(b);
        edTotalSale.setEnabled(b);
        edAadhar.setEnabled(b);
        edAddMobileNumber.setEnabled(b);
        edTotalCounter.setEnabled(b);
        edTin.setEnabled(b);
        edLandline.setEnabled(b);
        edGst.setEnabled(b);

        //ivIndentiity.setEnabled(b);
        ivShopImg.setEnabled(b);
        ivRetailerProfile.setEnabled(b);
        ivRetailerSignature.setEnabled(b);
        ivAadhar.setEnabled(b);
        ivVoterId.setEnabled(b);
        ivPan.setEnabled(b);
        ivDriving.setEnabled(b);
        ivDOB.setEnabled(b);
        tvDOB.setEnabled(b);

        ivShopImg.setOnClickListener(this);
        ivRetailerSignature.setOnClickListener(this);
        ivRetailerProfile.setOnClickListener(this);

        ivAadhar.setOnClickListener(this);
        ivVoterId.setOnClickListener(this);
        ivDriving.setOnClickListener(this);
        ivPan.setOnClickListener(this);
        ivDOB.setOnClickListener(this);

        btnAddRetailer = (Button) findViewById(R.id.btnAddRetailer);
        btnAddRetailer.setOnClickListener(this);

        btnAddRetailer.setEnabled(b);
        spLocality = (Spinner) findViewById(R.id.locality);
        spLocality.setEnabled(b);
        spGstType = (Spinner) findViewById(R.id.spGstType);
        spGstType.setEnabled(b);
        edPincode.setEnabled(b);
    }

}
