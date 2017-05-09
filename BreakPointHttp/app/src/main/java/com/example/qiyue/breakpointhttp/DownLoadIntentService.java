package com.example.qiyue.breakpointhttp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownLoadIntentService extends IntentService {
    private static final String TAG = DownLoadIntentService.class.getSimpleName();

    public static final String BROADCAST_INTENT_FILTER = "BroadcastIntentFilter";
    public static final String DOWNLOAD_PROGRESS = "Download_Progress";
    private int mDownloadSize;
    private int currSize;
    private int progress;
    public interface OnProgressListener {
        /**
         * 下载进度
         *
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
    public static void startSingleThreadDownLoad(Context context) {
        Intent intent = new Intent(context, DownLoadIntentService.class);
        context.startService(intent);
    }

    public Handler downLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (mDownloadSize > 0) {
                    currSize = (int) msg.obj;
                    progress = (100 * currSize) / mDownloadSize;
                    Intent intent = new Intent(BROADCAST_INTENT_FILTER);
                    intent.putExtra(DOWNLOAD_PROGRESS,progress);
                    sendBroadcast(intent);
                }
            }
        }
    };


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleActionFoo();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        InputStream inputStream;
        FileOutputStream fileOutputStream = null;
//        BufferedReader reader = null;
//        BufferedWriter writer = null;
        //需先创建目录--在创建文件
        File dir = new File(getExternalCacheDir().getPath());
        if( !dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir , "download.apk");
        int total = 0;
        int count ;
         /* 判断sd的外部设置状态是否可以读写 */
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                fileOutputStream = new FileOutputStream(file);

                URL url = new URL("http://i.ahggwl.com/hytpay/hyt.apk");
                URLConnection connection = url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
                mDownloadSize = connection.getContentLength();
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//                writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
                byte[] buf = new byte[1024];
                while ((count = inputStream.read(buf)) != -1) {
                    total += count;
                    fileOutputStream.write(buf,0,count);
                    downLoadHandler.sendMessage(Message.obtain(downLoadHandler, 1, total));
                }
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
//                        writer.close();
//                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
