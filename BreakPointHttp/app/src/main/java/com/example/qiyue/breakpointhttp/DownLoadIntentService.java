package com.example.qiyue.breakpointhttp;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownLoadIntentService extends IntentService {
    private static final String TAG = DownLoadIntentService.class.getSimpleName();
    private static final String ACTION_FOO = "com.example.qiyue.breakpointhttp.action.FOO";
    private static final String ACTION_BAZ = "com.example.qiyue.breakpointhttp.action.BAZ";

    private static final String EXTRA_PARAM1 = "com.example.qiyue.breakpointhttp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.qiyue.breakpointhttp.extra.PARAM2";


    public interface OnProgressListener {
        /**
         * 下载进度
         * @param fraction 已下载/总大小
         */
        void onProgress(float fraction);
    }

    public DownLoadIntentService() {
        super("DownLoadIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSingleThreadDownLoad(Context context, String apkUrl, String downloadPath) {
        Intent intent = new Intent(context, DownLoadIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, apkUrl);
        intent.putExtra(EXTRA_PARAM2, downloadPath);
        context.startService(intent);
    }

    public Handler downLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String apkUrl) {

    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
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

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
