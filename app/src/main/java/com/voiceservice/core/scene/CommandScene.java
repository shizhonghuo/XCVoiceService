package com.voiceservice.core.scene;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import ecarx.view.KeyEvent;

import org.json.JSONException;
import org.json.JSONObject;

import ecarx.app.CarSignalManager;
import ecarx.content.IntentHelper;
import ecarx.media.ECarXAudioManager;
import ecarx.media.policy.ECarXAudioPolicyManager;
import ecarx.voiceservice.VoiceSvrManager;

/**
 * Created by Administrator on 2017/7/21.
 */

public class CommandScene extends BaseScene{
    private static final String TAG="CommandScene";
    private static final String JSON_NAME="name";
    private static final String JSON_NAME_VALUE="nameValue";
    private static final String JSON_CATEGORY="ccategory";
    private static final String JSON_OPERATION="operation";
    private static final String JSON_MODE="mode";
    private CarSignalManager mCarSignalManager;
    private ECarXAudioManager eCarXAudioManager;
    private AudioManager mAudioManager;

    public CommandScene(Context context , Handler handler){
        super.BaseScene(context,handler);
        mSceneType= BaseScene.SCENE_TYPE_CMD;
        mCarSignalManager=CarSignalManager.get(context);
        eCarXAudioManager=new ECarXAudioManager(context);
        mAudioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int handleScene (String actionJson){
        try {
            JSONObject actionObj = new JSONObject(actionJson);
            // 只处理Command的
            if (BaseScene.JSON_FOCUS_TAG_CMD.equals(actionObj.getString(BaseScene.JSON_TAG_KEY_FOCUS))) {
                String rawText = "";
                if (actionObj.has(JSON_TAG_KEY_RAWTEXT)) {
                    rawText = actionObj.getString(JSON_TAG_KEY_RAWTEXT);
                }
                String name = "";
                if (actionObj.has(JSON_NAME)) {
                    name = actionObj.getString(JSON_NAME);
                }
                String nameValue = "";
                if (actionObj.has(JSON_NAME_VALUE)) {
                    nameValue = actionObj.getString(JSON_NAME_VALUE);
                }
                String category = "";
                if (actionObj.has(JSON_CATEGORY)) {
                    category = actionObj.getString(JSON_CATEGORY);
                }

                String operation = "";
                if (actionObj.has(JSON_OPERATION)) {
                    operation = actionObj.getString(JSON_OPERATION);
                }

                String mode = "";
                if (actionObj.has(JSON_MODE)) {
                    mode = actionObj.getString(JSON_MODE);
                }
                int cmdType = -1;
                int action = -1;
                if (!TextUtils.isEmpty(category)) {
                    if (category.equals("曲目控制") || category.equals("播放模式")) {
                        if (category.equals("曲目控制")) {
                            cmdType = VoiceSvrManager.COMMAND_TYPE_TRACKS_CONTROL;
                        } else if (category.equals("播放模式")) {
                            cmdType = VoiceSvrManager.COMMAND_TYPE_PLAY_MODE_CONTROL;
                        }

                        if (name.equals("上一首")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_PRE;
                        } else if (name.equals("下一首")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_NEXT;
                        } else if (name.equals("播放")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_PLAY;
                        } else if (name.equals("停止")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_STOP;
                        } else if (name.equals("暂停")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_PAUSE;
                        } else if (name.equals("单曲循环")) {
                            action = VoiceSvrManager.PLAY_MODE_SINGLE;
                        } else if (name.equals("随机播放")) {
                            action = VoiceSvrManager.PLAY_MODE_RANDOM;
                        } else if (name.equals("顺序循环")) {
                            action = VoiceSvrManager.PLAY_MODE_LOOP;
                        } else if (name.equals("快进")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_FAST_FORWARD;
                        } else if (name.equals("快退")) {
                            action = VoiceSvrManager.TRACKS_CONTROL_FAST_BACKWARD;
                        }
                    } else if (category.equals("音量控制")) {
                        cmdType = VoiceSvrManager.COMMAND_TYPE_VOLUME_CONTROL;
                        if (name.equals("打开音量")) {
                            action = VoiceSvrManager.VOLUME_CONTROL_UNMUTE;
                        } else if (name.equals("静音")) {
                            action = VoiceSvrManager.VOLUME_CONTROL_MUTE;
                        } else if (name.equals("音量+")) {
                            action = VoiceSvrManager.VOLUME_CONTROL_UP;
                        } else if (name.equals("音量-")) {
                            action = VoiceSvrManager.VOLUME_CONTROL_DOWN;
                        }
                    }
                }
                if(cmdType== -1 || action == -1){
                    return  BaseScene.HANDLER_ERROR;
                } else {
                    boolean flag= doProcessCmdAction(cmdType,action,StringToInt(nameValue,1));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return 0;
    }

    private boolean doProcessCmdAction(int CMDType, int action, int actionValue){
        switch (CMDType){
            case VoiceSvrManager.COMMAND_TYPE_PLAY_MODE_CONTROL:{
                switch (action){
                    case VoiceSvrManager.PLAY_MODE_LOOP:{  //循环播放
                        sendMusicControl(IntentHelper.ECARX_ACTION_MUSIC_LOOP);
                        break;
                    }
                    case VoiceSvrManager.PLAY_MODE_RANDOM: //随机播放
                        sendMusicControl(IntentHelper.ECARX_ACTION_MUSIC_RANDOM);
                        return true;
                    case VoiceSvrManager.PLAY_MODE_SINGLE: //单曲循环
                        sendMusicControl(IntentHelper.ECARX_ACTION_MUSIC_SINGLE);
                        return true;
                    default:
                        break;
                }
                break;
            }
            case VoiceSvrManager.COMMAND_TYPE_VOLUME_CONTROL:{
                int volume =0;
                switch (action){
                    case VoiceSvrManager.VOLUME_CONTROL_DOWN:{ //减小音量
                        volume =mAudioManager.getStreamVolume(ECarXAudioPolicyManager.STREAM_MUSIC);
                        if(volume-actionValue >=0){
                            volume-=actionValue;
                        } else {
                            volume=0;
                        }
                        mAudioManager.setStreamVolume(ECarXAudioPolicyManager.STREAM_MUSIC,volume,AudioManager.FLAG_SHOW_UI);
                        break;
                    }
                    case VoiceSvrManager.VOLUME_CONTROL_UP:{  //增大音量
                        volume =mAudioManager.getStreamVolume(ECarXAudioPolicyManager.STREAM_MUSIC);
                        if(volume+actionValue<=32){
                            volume+=actionValue;
                        } else {
                            volume= 32;
                            boolean mute =mAudioManager.isStreamMute(ECarXAudioPolicyManager.STREAM_MUSIC);
                            // 如果处于mute状态， 将mute关闭
                            if (mute == true){
                                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC,false);
                                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM,false);
                                mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION,false);
                                mAudioManager.setStreamMute(ECarXAudioPolicyManager.STREAM_VOICE,false);
                                mAudioManager.setStreamMute(ECarXAudioPolicyManager.STREAM_WALKIETALKIE,false);
                                mAudioManager.setStreamMute(ECarXAudioPolicyManager.STREAM_NAV,false);
                            }
                        }
                        mAudioManager.setStreamVolume(ECarXAudioPolicyManager.STREAM_MUSIC,volume,AudioManager.FLAG_SHOW_UI);
                        break;
                    }
                    case VoiceSvrManager.VOLUME_CONTROL_MUTE:{
                        eCarXAudioManager.setMute(true);
                        break;
                    }
                    case VoiceSvrManager.VOLUME_CONTROL_UNMUTE:{
                        eCarXAudioManager.setMute(false);
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case VoiceSvrManager.COMMAND_TYPE_TRACKS_CONTROL:{
                switch(action){
                    case VoiceSvrManager.TRACKS_CONTROL_NEXT:{
                        injectKeyCode(KeyEvent.KEYCODE_ECARX_MEDIA_NEXT);
                        break;
                    }
                    case VoiceSvrManager.TRACKS_CONTROL_PRE:
                        injectKeyCode(KeyEvent.KEYCODE_ECARX_MEDIA_PREVIOUS);
                        return true;
                    case VoiceSvrManager.TRACKS_CONTROL_PLAY:
                        injectKeyCode(android.view.KeyEvent.KEYCODE_MEDIA_PLAY);
                        return true;
                    case VoiceSvrManager.TRACKS_CONTROL_PAUSE:
                        injectKeyCode(android.view.KeyEvent.KEYCODE_MEDIA_PAUSE);
                        return true;
                    case VoiceSvrManager.TRACKS_CONTROL_STOP:
                        injectKeyCode(android.view.KeyEvent.KEYCODE_MEDIA_STOP);
                        return true;
                    case VoiceSvrManager.TRACKS_CONTROL_FAST_FORWARD:
                        //TODO 快进
                        return true;
                    case VoiceSvrManager.TRACKS_CONTROL_FAST_BACKWARD:
                        //TODO 快退
                        return true;
                    default:
                        break;
                }
            }
        }
        return  true;
    }

    private void injectKeyCode(int keyCode){
        long now= SystemClock.uptimeMillis();
        injectKeyEvent(new android.view.KeyEvent(now,now,android.view.KeyEvent.ACTION_DOWN,keyCode,0));
        now= SystemClock.uptimeMillis();
        injectKeyEvent(new android.view.KeyEvent(now,now,android.view.KeyEvent.ACTION_UP,keyCode,0));
    }

    private void injectKeyEvent(android.view.KeyEvent ev){
        mCarSignalManager.injectKeyEvent(ev);
    }


    private void sendMusicControl(String action){
        Intent intent= new Intent(action);
        context.sendBroadcast(intent);
    }

    private  int StringToInt(String valueStr, int defaultValue){
        int value= defaultValue;
        if( !TextUtils.isEmpty(valueStr)){
            value= Integer.parseInt(valueStr);
        }
        return  value;
    }

}
