package com.voiceservice.policy;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;

import com.iflytek.platformservice.PlatformService;

import java.util.List;

import ecarx.bluetooth.ECarXBluetoothCarHeadset;
import ecarx.media.ECarXAudioManager;
import ecarx.media.policy.ECarXAudioInfo;
import ecarx.media.policy.ECarXAudioPolicyManager;

/**
 * Created by Administrator on 2017/7/17.
 */

public class AudioPolicyHelper {
    private String TAG ="AudioPolicyHelper";
    private final int VOICE_STREAM = ECarXAudioPolicyManager.STREAM_VOICE;
    public static  final String PACKAGE_NAME="ecarx.voiceservice";
    private ECarXAudioPolicyManager mAudioPolicy;
    private static AudioPolicyHelper mInstance;
    private boolean mRegister =false;
    private Context mContext;

    private static final int[] PRIORITY_STREAM ={
      ECarXAudioPolicyManager.STREAM_VOICE,ECarXAudioPolicyManager.STREAM_VOICE_CALL,
      ECarXAudioPolicyManager.STREAM_RING
    };

    private AudioPolicyHelper(Context context){
        mContext=context;
        mAudioPolicy=new ECarXAudioPolicyManager(context);
    }

    public static AudioPolicyHelper getmInstance(Context context){
        if(mInstance == null){
            mInstance =new AudioPolicyHelper(context);
        }
        return mInstance;
    }

    public void register (){
        if( mAudioPolicy == null){
            mAudioPolicy = new ECarXAudioPolicyManager(mContext);
        }
        int ret = mAudioPolicy.register(PACKAGE_NAME, VOICE_STREAM,listener);
        mAudioPolicy.setProp(ECarXAudioPolicyManager.PROP_MUTEX,ECarXAudioPolicyManager.PROPV_ENABLE);
    }
    public boolean play(){
        if(!mRegister){
            register();
            mRegister = true;
        }
        int result= mAudioPolicy.play(VOICE_STREAM);
        if(result == ECarXAudioPolicyManager.NO_ERROR){
            return checkVoiceStream();
        }
        return false;
    }

    public void stop(){
        if(mRegister){
            mAudioPolicy.stop();

        }
    }

    public void unregister(){
        mRegister= false;
        mAudioPolicy.unregister();
    }

    public boolean checkVoiceStream(){
        return checkAudioStream(VOICE_STREAM);
    }

    public boolean checkAudioStream(int streamType){
        List<ECarXAudioInfo> list = mAudioPolicy.getAllStreams(1);
        if(list.size() >0){
            for(ECarXAudioInfo info:list){
                if(info.getStreamType() == streamType){
                    return true;
                }
            }
        }
        return false;
    }


    private ECarXAudioPolicyManager.ECarXAudioPolicyListener listener =new ECarXAudioPolicyManager.ECarXAudioPolicyListener(){
        public void handleMessage(int msg,String realName,String reasonName,int arg1,int arg2){
            switch(msg){
                case ECarXAudioPolicyManager.MSG_PLAY:
                    break;
                case ECarXAudioPolicyManager.MSG_STOP:{
                    if(reasonName != null&&!reasonName.equals(PACKAGE_NAME)){
                         if(PlatformService.platformCallback == null){
                             return;
                         }
                         try {
                             PlatformService.platformCallback.audioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
                         } catch (RemoteException e){
                             e.printStackTrace();
                         }
                    }
                    break;
                }
                case ECarXAudioPolicyManager.MSG_KILL:{
                    if(reasonName != null && !reasonName.equals(PACKAGE_NAME)){
                        mRegister = false;
                        try {
                            PlatformService.platformCallback.audioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
                        } catch (RemoteException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };


}
