package com.example.qiyue.breakpointhttp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BreakPointService extends Service {
    public BreakPointService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
