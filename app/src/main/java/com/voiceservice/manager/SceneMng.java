package com.voiceservice.manager;

import android.content.Context;
import android.os.Handler;

import com.voiceservice.core.scene.AppScene;
import com.voiceservice.core.scene.BaseScene;
import com.voiceservice.core.scene.CommandScene;
import com.voiceservice.core.scene.MusicScene;
import com.voiceservice.core.scene.RadioScene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/20.
 */

public class SceneMng {
    private static final String TAG= "SceneMng";
    private static volatile  SceneMng mInstance= null;
    private Context mContext;
    private Map<Integer,BaseScene> mSceneHandle = new HashMap<>();
    private Handler mMainHandle;

    private SceneMng(Context context, Handler uiHandle){
        mContext=context;
        mMainHandle=uiHandle;
        initAllSceneHandle();
    }

    public static SceneMng getInstance(Context context, Handler handler){
        if( mInstance == null){
            synchronized (SceneMng.class){
                mInstance=new SceneMng(context,handler);
            }
        }
        return  mInstance;
    }



    private void initAllSceneHandle(){
        mSceneHandle.clear();
        mSceneHandle.put(BaseScene.SCENE_TYPE_APP,new AppScene(mContext,this.mMainHandle));
        mSceneHandle.put(BaseScene.SCENE_TYPE_CMD,new CommandScene(mContext,this.mMainHandle));
        mSceneHandle.put(BaseScene.SCENE_TYPE_MUSIC,new MusicScene(mContext,this.mMainHandle));
        mSceneHandle.put(BaseScene.SCENE_TYPE_RADIO,new RadioScene(mContext,this.mMainHandle));
    }

    public boolean handlerScene(String stringJson){
        boolean flag= false;
        Iterator it= mSceneHandle.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            Object val=entry.getValue();
            if(val instanceof  BaseScene){
                int result= ((BaseScene) val).handleScene(stringJson);
                if( result == BaseScene.HANDLER_OK){
                    flag=true;
                    break;
                }
            }
        }
        return flag;
    }
}
