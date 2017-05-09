package com.example.qiyue.breakpointhttp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

    @BindView(R.id.tvProgressText)
    TextView mProgressText;

    private static final String APK_URL = "http://i.ahggwl.com/hytpay/hyt.apk";
    private static final String DOWN_LOAD_PATH = Environment.DIRECTORY_DOWNLOADS;

    private String TAG = this.getClass().getSimpleName();

    private boolean isBindService;

    private int mProgressSize;

    private boolean isPermission = false;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            DownloadService downloadService = binder.getService();

            //接口回调，下载进度
            downloadService.setOnProgressListener(new DownloadService.OnProgressListener() {
                @Override
                public void onProgress(float fraction) {
                    mProgressSize = (int) (fraction * 100);
                    mProgressText.setText(mProgressSize+"%");
                    mProgressBar.setProgress(mProgressSize);

                    //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                    if (fraction == DownloadService.UNBIND_SERVICE && isBindService) {
                        unbindService(conn);
                        isBindService = false;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public void bindService() {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, APK_URL);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_PATH, DOWN_LOAD_PATH);
        isBindService = bindService(intent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        registerBroadcast();
    }

    @OnClick({R.id.tvSingleThreadDownload, R.id.tvMoreThreadDownload, R.id.tvBreakPointHttp})
    public void onClick(View v){
        verifyStoragePermissions(MainActivity.this);
        switch (v.getId()){
            case R.id.tvSingleThreadDownload:
                if(isPermission){
                    bindService();
                }
                break;
            case R.id.tvMoreThreadDownload:
                if(isPermission){
                    DownLoadIntentService.startSingleThreadDownLoad(MainActivity.this);
                }
                break;
            case R.id.tvBreakPointHttp:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else {
            isPermission = true;
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
                    isPermission = true;
                }else {
                    isPermission = false;
                }
                return;
            }
        }
    }

    /**
     * 删除上次更新存储在本地的apk
     */
    private void removeOldApk() {
        //获取老ＡＰＫ的存储路径
//        File fileName = new File(SPUtil.getString(Constant.SP_DOWNLOAD_PATH, ""));
//        LogUtil.i(TAG, "老APK的存储路径 =" + SPUtil.getString(Constant.SP_DOWNLOAD_PATH, ""));
//
//        if (fileName != null && fileName.exists() && fileName.isFile()) {
//            fileName.delete();
//            LogUtil.i(TAG, "存储器内存在老APK，进行删除操作");
//        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownLoadIntentService.BROADCAST_INTENT_FILTER);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 注销广播
     */
    private void unregisterBroadcast() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressSize = intent.getIntExtra(DownLoadIntentService.DOWNLOAD_PROGRESS,0);
            mProgressText.setText(mProgressSize+"%");
            mProgressBar.setProgress(mProgressSize);
        }
    };

}
