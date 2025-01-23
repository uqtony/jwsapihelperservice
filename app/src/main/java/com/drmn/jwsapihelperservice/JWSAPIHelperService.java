package com.drmn.jwsapihelperservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
public class JWSAPIHelperService extends Service {
    public static final String TAG = JWSAPIHelperService.class.getSimpleName();

    public class JWSAPIHelperBinder extends Binder {
        public JWSAPIHelperService getService() {
            return JWSAPIHelperService.this;
        }
    }

    private final JWSAPIHelperBinder binder = new JWSAPIHelperBinder();
    public JWSAPIHelperService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: thread=["+ Thread.currentThread().getName()+"]");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: thread=["+ Thread.currentThread().getName()+"]");
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStartCommand: thread=["+Thread.currentThread().getName()+"]");
        try {
            String command = intent.getStringExtra(JWSAPICommand.COMMAND_COMMAND);
            String[] messages = intent.getStringArrayExtra(JWSAPICommand.COMMAND_MESSAGES);
            JWSAPIImplement.getInst(this).execute(command, messages);
        }catch (Exception e) {
            Log.e(TAG, "Error:"+e.getMessage());
        }finally {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: thread=["+Thread.currentThread().getName()+"]");
    }

    public String executeJWSApi(String command, String[] message){
        return JWSAPIImplement.getInst(this).execute(command, message);
    }

}