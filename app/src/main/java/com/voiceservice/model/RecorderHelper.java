package com.voiceservice.model;

import android.content.Context;
import android.content.SharedPreferences;

import ecarx.voiceservice.VoiceSvrManager;

/**
 * Created by Administrator on 2017/7/20.
 */

public class RecorderHelper {
    public interface SpeakerKey{
        public static final String SPEAKER_ID="ecarx.tts.speaker";
        public static final int DEFAULT_ID=VoiceSvrManager.TTS_ROLE_USER;
    }

    private static String FILE_NAME ="voicePrefer";
    private Context mContext;
    private SharedPreferences mSharePreferces;
    private static RecorderHelper mInstance;

    public static RecorderHelper getInstance(Context context){
        if(mInstance == null){
            synchronized (RecorderHelper.class){
                mInstance=new RecorderHelper(context);
            }
        }
        return  mInstance;
    }

    /*
    * sharePreferces 有三种mode;
    * 1.MODE_PRIVATE  最安全， 只能够被创建的application 读写
    * 2. MODE_WORLD_READABLE 可以被所有application 读
    * 3. MODE_WORLD_WRITEABLE 可以被所有application 写
     */
    private RecorderHelper(Context context){
        mContext=context;
        mSharePreferces = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
    }

    public void saveSpeaker(int speakerId){
        mSharePreferces.edit().putInt(SpeakerKey.SPEAKER_ID,speakerId);
        mSharePreferces.edit().commit();
    }

    public int getSpeaker(){
        return mSharePreferces.getInt(SpeakerKey.SPEAKER_ID,SpeakerKey.DEFAULT_ID);
    }
}
