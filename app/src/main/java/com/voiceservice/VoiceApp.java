package com.voiceservice;

import android.app.Application;
import android.content.Intent;

import ecarx.content.IntentHelper;
import ecarx.os.SystemProperties;

/**
 * Created by Administrator on 2017/7/13.
 */

public class VoiceApp extends Application {
    private static VoiceApp instance;
    public void onCreate(){
        super.onCreate();
        instance=this;
        if("1".equals(SystemProperties.get("sys.boot_complete"))){
            Intent serviceIntent=new Intent();
            serviceIntent.setAction(IntentHelper.ECARX_ACTION_VOICE_SERVICE_STARTED);
            serviceIntent.addCategory(IntentHelper.ECARX_CATEGORY_VOICE_SERVICE_STARTED);
            startService(serviceIntent);
        }
    }
}
