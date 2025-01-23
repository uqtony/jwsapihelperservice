package com.drmn.jwsapihelperservice;

import android.content.Context;
import android.util.Log;

import com.android.jws.JwsManager;

import java.util.HashMap;
import java.util.Map;

public class JWSAPIImplement {
    private static  final String TAG = JWSAPIImplement.class.getSimpleName();
    public interface FunctionPointer {
        String execute(String[] messages);
    }
    private static JWSAPIImplement _inst = null;
    private static final Object lock = new Object();
    private JwsManager jwsManager = null;
    private Context _context;


    public static JWSAPIImplement getInst(Context context) {
        synchronized (lock){
            if (_inst == null){
                _inst = new JWSAPIImplement(context);
            }
        }
        return _inst;
    }

    private final Map<String, FunctionPointer> apiFunctionMap = new HashMap<>();

    public String execute(String command, String[] messages) {
        if (!apiFunctionMap.containsKey(command)){
            Log.e(TAG, "execute: error! no such command: command="+command);
            return null;
        }
        try {
            return apiFunctionMap.get(command).execute(messages);
        }catch (NullPointerException e){
            Log.e(TAG, "execute: error! null pointer exception! e="+e.getMessage());
            return null;
        }
    }

    private JWSAPIImplement(Context context) {
        _context = context;
        initJws(context);
        initAPIMap();
    }

    private void initJws(Context context) {
        jwsManager = JwsManager.create(context);
    }

    private void initAPIMap() {
        apiFunctionMap.put(JWSAPICommand.COMMAND_GET_ANDROID_MODEL, this::getAndroidModel);
        apiFunctionMap.put(JWSAPICommand.COMMAND_SILENT_INSTALL, this::silentInstall);
        apiFunctionMap.put(JWSAPICommand.COMMAND_SCREENSHOT, this::takeScreenshot);
    }

    private String getAndroidModel(String[] messages){
        return jwsManager.jwsGetAndroidVersion();
    }
    private String takeScreenshot(String[] messages) {
        jwsManager.jwsTakeScreenshot(messages[0], messages[1], _context);
        return "ok";
    }

    private String silentInstall(String[] messages){
        jwsManager.jwsSilentInstall(messages[0], _context);
        return "ok";
    }
}
