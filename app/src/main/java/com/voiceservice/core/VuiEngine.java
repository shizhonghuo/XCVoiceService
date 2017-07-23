package com.voiceservice.core;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.iflytek.platform.PlatformCallBackListener;
import com.iflytek.platform.PlatformClientListener;
import com.iflytek.platform.aidl.PlatformAidlService;
import com.iflytek.platform.type.PlatformCode;
import com.iflytek.platformservice.PlatformService;
import com.voiceservice.R;
import com.voiceservice.core.frontvoice.FrontVoiceWorkMode;
import com.voiceservice.manager.BluetoothMng;
import com.voiceservice.manager.SceneMng;
import com.voiceservice.manager.VoiceResMng;
import com.voiceservice.model.RecorderHelper;
import com.voiceservice.policy.AudioPolicyHelper;
import com.voiceservice.service.ServiceProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ecarx.media.policy.ECarXAudioPolicyManager;
import ecarx.voiceservice.VoiceSvrManager;
import ecarx.voiceservice.role.SpeakerRole;

/**
 * Created by Administrator on 2017/7/13.
 */

public class VuiEngine implements PlatformClientListener{
    private Context context;
    private static VuiEngine sInstance=null;
    private Handler handler;
    private AudioPolicyHelper mAudioHelp;
    private AudioManager audioManager;
    private int mCurrentSpeakerId;
    private RecorderHelper mRdHelper=null;
    private VoiceResMng mVoiceResMng;
    private SceneMng mSceneMng;
    private BluetoothMng mBMng;
    private List<SpeakerRole> mSpeakerRoles = new ArrayList<SpeakerRole>();


    private VoiceServiceOnAudioFocusChangeListener listener;

    private class VoiceServiceOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener{
        public void onAudioFocusChange(int focusChange){
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS){

            }
        }

    }


    private VuiEngine(Context context,Handler handler){
        this.context=context;
        this.handler = handler;
        audioManager= (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        mRdHelper=RecorderHelper.getInstance(context);
        mVoiceResMng=VoiceResMng.getmInstance(context);
        mSceneMng=SceneMng.getInstance(context,handler);
        mBMng=BluetoothMng.getsInstance(context);
    }

    public static VuiEngine getsInstance(Context context, Handler handler){
        synchronized (sInstance){
            if(sInstance  == null){
                sInstance=new VuiEngine(context, handler);
            }
        }
        return sInstance;
    }

    private void initialAllSpeakerRoles(){
        mSpeakerRoles.clear();
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_YANYAN, context.getString(R.string.xiaoyan_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOFENG, context.getString(R.string.xiaofeng_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_NANNAN, context.getString(R.string.nannan_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_JIAJIA, context.getString(R.string.jiajia_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOQIAN, context.getString(R.string.xiaoqian_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAORONG, context.getString(R.string.xiaorong_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOMEI, context.getString(R.string.xiaomei_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOLIN, context.getString(R.string.xiaolin_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOQIANG, context.getString(R.string.xiaoqiang_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOKUN, context.getString(R.string.xiaokun_title)));
        mSpeakerRoles.add(new SpeakerRole(VoiceSvrManager.TTS_ROLE_XIAOXUE, context.getString(R.string.xiaoxue_title)));
    }

    private void initialCurrentSpeakerId(){
        mCurrentSpeakerId=mRdHelper.getSpeaker();
        if(PlatformService.platformCallback != null){
            try {
                PlatformService.platformCallback.changeSettings(PlatformCode.SETTING_SPEAKERS, mCurrentSpeakerId);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }
   public  String onNLPResult(String actionJson){
       JSONObject resultJson = new JSONObject();
       boolean result =mSceneMng.handlerScene(actionJson);
       try{
           if(result){
               resultJson.put("status","success");
            } else {
               resultJson.put("status","fail");
               resultJson.put("Message","抱歉，没有可处理的操作");
           }
       } catch (JSONException e){
           e.printStackTrace();
       }
       return resultJson.toString();
   }

   public  String onDoAction(String actionJson){
       JSONObject resultJson = new JSONObject();
       try {
           if (actionJson == null) {
               resultJson.put("status", "fail");
               resultJson.put("message","抱歉，没有可处理的操作");
           } else {
               JSONObject action = new JSONObject(actionJson);
               if(action.getString("action").equals("call")){
                   if(action.getString("param1")!= null){
                       String number= action.getString("param1");
                       if(mBMng.doStartBTCall(number)){
                           resultJson.put("status","success");
                       } else {
                           resultJson.put("status", "fail");
                           resultJson.put("message","抱歉，没有可处理的操作");
                       }
                   } else{
                       resultJson.put("status", "fail");
                   }
               } else if (action.getString("action").equals("startspeekrecord")){
                   if(mVoiceResMng.prvChangeMode(FrontVoiceWorkMode.WORK_MODE_NOISECLEAN)==0){
                       resultJson.put("status","success");
                   } else {
                       resultJson.put("status", "fail");
                   }
               } else if(action.getString("action").equals("stopspeedrecord")){
                   resultJson.put("status","success");
               } else if (action.getString("action").equals("startwakerecord")) {
                   if(mVoiceResMng.prvChangeMode(FrontVoiceWorkMode.WORK_MODE_VOICE_WAKEUP)==0){
                       resultJson.put("status","success");
                   } else {
                       resultJson.put("status", "fail");
                   }
               }
           }
       } catch ( JSONException e){
           e.printStackTrace();
       }
       return resultJson.toString();
   }

    public int onGetState(int state){
        if(state == PlatformCode.STATE_BLUETOOTH_PHONE){ //获得蓝牙状态
            boolean btPhoneState=mBMng.isHFPConnected();
            if(btPhoneState){
                return  PlatformCode.STATE_OK;
            } else{
                return  PlatformCode.STATE_NO;
            }
        }
        return  0;
    }


    public String onGetLocation(){
        String poiName = null;
        String address = null;
        String cityName = null;
        double longitude = 0.0f;
        double latitude = 0.0f;
        return null;
    }

   public int onRequestAudioFocus(int var1, int var2){
       if(mAudioHelp == null) {
           mAudioHelp=AudioPolicyHelper.getmInstance(context);
       }
       if(mAudioHelp.play()){
           sendAudioControl(true);
           return AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
       }
       return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
   }


   public void onAbandonAudioFocus(){
       doAbandonAudioFocus();
   }


    public void onServiceUnbind(){
        doAbandonAudioFocus();
        notifyServiceUnBinder();
    }

    public int changePhoneState(int state){
        if( mBMng.handlerUserChangePhoneStateByVoice(state)){
            return PlatformCode.SUCCESS;
        } else {
            return  PlatformCode.FAILED;
        }
    }


    public String onGetCarNumbersInfo(){
        return null;
    }


   public  boolean onSearchPlayList(String var1){
       return true;
   }

    public boolean notifySettingChange(int commandId, int value){
        switch(commandId){
            case PlatformCode.SETTING_SPEAKERS:{
                mCurrentSpeakerId=value;
                mRdHelper.saveSpeaker(mCurrentSpeakerId);
            }
        }
        return true;
    }

    private void notifyServiceUnBinder(){
        handler.sendEmptyMessage(ServiceProcessor.MSG_VUI_SERVER_RELOAD);
    }
    private void sendAudioControl(boolean isStart){

        Intent intent= new Intent();
        if(isStart){
            intent.setAction("com.iflytek.startoperation");
            int result = audioManager.requestAudioFocus(listener,
                    ECarXAudioPolicyManager.STREAM_VOICE,audioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        } else{
            audioManager.abandonAudioFocus(listener);
            intent.setAction("com.iflytek.endoperation");
        }
        context.sendBroadcast(intent);
    }
    private void doAbandonAudioFocus(){
        if(mAudioHelp == null){
            mAudioHelp= AudioPolicyHelper.getmInstance(context);
        }
        mAudioHelp.stop();
        sendAudioControl(false);
    }



    public List<SpeakerRole> getAllSpeakerRoles() throws RemoteException{
        if(mSpeakerRoles ==null || mSpeakerRoles.size()==0){
            initialAllSpeakerRoles();
        }
        return mSpeakerRoles;
    }


    public boolean setSpeakerID(int var1) throws RemoteException{
        mRdHelper.saveSpeaker(var1);
        mCurrentSpeakerId =var1;
        PlatformService.platformCallback.changeSettings(PlatformCode.SETTING_SPEAKERS,var1);

        return true;
    }

    public int getSpeakerID() throws RemoteException{
        return mCurrentSpeakerId;
    }

    public boolean resetWakeUpSource() {
        try {
            PlatformService.platformCallback.changeSettings(PlatformCode.SETTING_REVERT_WAKEUP, 0);
        }catch ( RemoteException e){
            e.printStackTrace();
        }
        return true;
    }


    public boolean requestMic(int var1, String var2, IBinder var3) throws RemoteException{
        return mVoiceResMng.requestMic(var1,var2,var3);
    }


    public boolean releaseMic(int var1, String var2, IBinder var3) throws RemoteException{
        return mVoiceResMng.releaseMic(var1,var2,var3);
    }
}
