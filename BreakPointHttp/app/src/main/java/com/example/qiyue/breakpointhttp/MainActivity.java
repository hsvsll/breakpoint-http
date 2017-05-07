package com.example.qiyue.breakpointhttp;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tvSingleThreadDownload)
    Button mSingleThreadBt;
    @BindView(R.id.tvMoreThreadDownload)
    Button mMoreThreadBt;
    @BindView(R.id.tvBreakPointHttp)
    Button mBreakPointHttpBt;
    @BindView(R.id.pbDownLoad)
    ProgressBar mProgressBar;

    private static final String APK_URL = "http://i.ahggwl.com/hytpay/hyt.apk";
    private static final String DOWN_LOAD_PATH = "/Users/qiyue/Downloads/apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
    }

    @OnClick({R.id.tvSingleThreadDownload, R.id.tvMoreThreadDownload, R.id.tvBreakPointHttp})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tvSingleThreadDownload:
                verifyStoragePermissions(MainActivity.this);
//                DownLoadIntentService.startSingleThreadDownLoad(this,APK_URL,DOWN_LOAD_PATH);
                break;
            case R.id.tvMoreThreadDownload:
                Log.i("TAG","TEST ----------");
                break;
            case R.id.tvBreakPointHttp:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else {
            DownLoadIntentService.startSingleThreadDownLoad(MainActivity.this,APK_URL,DOWN_LOAD_PATH);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    DownLoadIntentService.startSingleThreadDownLoad(this,APK_URL,DOWN_LOAD_PATH);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
