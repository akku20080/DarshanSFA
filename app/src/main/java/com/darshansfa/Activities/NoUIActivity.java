package com.darshansfa.Activities;

import android.os.Bundle;

import com.darshansfa.R;
import com.darshansfa.Utility.UIUtil;


public class NoUIActivity extends RuntimePermissionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_ui);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UIUtil.isGPSON(this)) {
//            NoUIActivity.super.requestAppPermissions(new String[]{
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.CAMERA}, R.string.accept
//                    , 223);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

}
