package com.drmn.jwsapihelperservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: component="+name);
            if (service instanceof JWSAPIHelperService.JWSAPIHelperBinder) {
                JWSAPIHelperService.JWSAPIHelperBinder _binder = (JWSAPIHelperService.JWSAPIHelperBinder)service;
                //takeScreenshot(_binder.getService());
                silentInstall(_binder.getService());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: component="+name);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //bindJWSAPIHelperService();
        startJWSAPIHelperService();
    }

    @Override
    protected void onDestroy() {
        unbindJWSAPIHelperService();
        super.onDestroy();
    }

    private void bindJWSAPIHelperService() {
        Intent intent = new Intent(this, JWSAPIHelperService.class);
        intent.putExtra("command", JWSAPICommand.COMMAND_GET_ANDROID_MODEL);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void unbindJWSAPIHelperService() {
        unbindService(serviceConnection);
    }
    private void startJWSAPIHelperService() {
        Intent intent = new Intent(MainActivity.this, JWSAPIHelperService.class);
        intent.putExtra("command", JWSAPICommand.COMMAND_SILENT_INSTALL);
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getPath() + File.separator;
        String fileName = "test03_2.apk";
        String[] messages = new String[1];
        messages[0] = filePath + fileName;
        intent.putExtra(JWSAPICommand.COMMAND_MESSAGES, messages);
        startService(intent);
    }

    private void takeScreenshot(JWSAPIHelperService service) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getPath() + File.separator;
        SimpleDateFormat sdformats = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String fileName = sdformats.format(new Date(System.currentTimeMillis())) + ".png";
        String[] messages = new String[2];
        messages[0] = filePath;
        messages[1] = fileName;
        Log.d(TAG, "Call Jws API to take screenshot, path="+filePath+", name="+fileName);
        service.executeJWSApi(JWSAPICommand.COMMAND_SCREENSHOT, messages);
    }

    private void silentInstall(JWSAPIHelperService service) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getPath() + File.separator;
        String fileName = "test03_1.apk";
        String[] messages = new String[1];
        messages[0] = filePath + fileName;
        String result = service.executeJWSApi(JWSAPICommand.COMMAND_SILENT_INSTALL, messages);
        Log.d(TAG, "Call jwsapi to silent install, result="+result);
    }
}