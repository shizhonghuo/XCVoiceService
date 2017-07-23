package com.voiceservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

import ecarx.app.ContextHelper;
import ecarx.app.ServiceManagerHelper;
import ecarx.content.IntentHelper;

/**
 * Created by Administrator on 2017/7/13.
 */

public class VoiceService extends Service{
    private VoiceConnector mBinder=null;
    private static VoiceService sInstance;

    public void PublishServiceToSystem(){

        ServiceManagerHelper.addService(ContextHelper.ECARX_VOICE_SERVICE,mBinder);
        /* 发送初始化完成的广播通知*/
        Intent intent=new Intent(IntentHelper.ECARX_ACTION_VOICE_SERVICE_COMPLETED);
        this.sendBroadcast(intent);
    }

    public void onCreate(){
        super.onCreate();
        ServiceProcessor processor=ServiceProcessor.getInstance(this);
        mBinder=VoiceConnector.getConnector(processor);
        sInstance=this;
        PublishServiceToSystem();
    }

    public Binder onBind(Intent intent){
        if(IntentHelper.ECARX_ACTION_VOICE_SERVICE_STARTED.equals(intent.getAction())){
            for(String catgory:intent.getCategories()){
                if(catgory.equals(IntentHelper.ECARX_CATEGORY_VOICE_SERVICE_STARTED)){
                    return mBinder;
                }
            }
        }
        return null;
    }

    public void onDestroy(){
        mBinder=null;
        sInstance=null;
        super.onDestroy();
    }

    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

}
