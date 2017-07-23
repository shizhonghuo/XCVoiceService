package com.voiceservice.service;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.iflytek.platformservice.PlatformHelp;
import com.voiceservice.core.VuiEngine;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ecarx.voiceservice.IVoiceObserver;
import ecarx.voiceservice.IVoiceService;
import ecarx.voiceservice.role.SpeakerRole;
import ecarx.voiceservice.ITextObserver;
import ecarx.voiceservice.scene.QMusicResult;
import ecarx.voiceservice.scene.QRadioResult;

/**
 * Created by Administrator on 2017/7/13.
 */

public class ServiceProcessor {
    private Map<String ,IVoiceObserver> mObservers =new HashMap<>();
    private Map<Integer,ITextObserver> mTextObservers=new HashMap<>();
    private VuiEngine vuiEngine;
    private Context context;
    private static final int MSG_START_INIT_ADAPTER_LOOP=1;
    private static final int MSG_INIT_ADAPTER_OVER=2;
    public static final int MSG_SPEAKER_HINT_FOR_NO_SUPPORT = 3;
    public static final int MSG_VUI_SERVER_RELOAD = 4;
    public static final int MSG_CALLBACK_MUSIC_SCENE = 5;
    public static final int MSG_CALLBACK_RADIO_SCENE = 6;
    public static final int MSG_CALLBACK_TEXT_FROM_IFLYTEK=7;

    private static ServiceProcessor instance;
    private boolean isConnectToPlatform = false;

    private ServiceProcessor(Context context){
        this.context=context;
        vuiEngine=VuiEngine.getsInstance(context,mainHandle);
        mainHandle.sendEmptyMessage(MSG_START_INIT_ADAPTER_LOOP);
    }

   private Handler mainHandle = new Handler(){
       public void handleMessage(Message msg){
           switch(msg.what){
               case MSG_START_INIT_ADAPTER_LOOP:
                   if(vuiEngine == null){
                       sendEmptyMessageDelayed(MSG_START_INIT_ADAPTER_LOOP,300);
                   } else {
                       sendEmptyMessage(MSG_INIT_ADAPTER_OVER);
                   }
                   break;
               case MSG_INIT_ADAPTER_OVER:
                   PlatformHelp.getInstance().setPlatformClient(vuiEngine);
                   isConnectToPlatform=true;
                   break;
               case MSG_VUI_SERVER_RELOAD:
                   sendEmptyMessageDelayed(MSG_INIT_ADAPTER_OVER, 300);
               case MSG_CALLBACK_MUSIC_SCENE:
                   if( msg.obj != null && msg.obj instanceof QMusicResult){
                       allVoiceObserverInvoke("processMucsicScene",QMusicResult.class,(QMusicResult)msg.obj);
                   }
                   break;
               case MSG_CALLBACK_RADIO_SCENE:
                   if(msg.obj != null && msg.obj instanceof QRadioResult){
                       allVoiceObserverInvoke("processRadioScene",QRadioResult.class,(QRadioResult)msg.obj);
                   }
           }
       }
   };

    public boolean isConnected()throws RemoteException {
        return (vuiEngine != null)&& isConnectToPlatform;
    }

    private class VoiceObserverDeathRecipient implements  IBinder.DeathRecipient{
        private IVoiceObserver mObserver;

        public VoiceObserverDeathRecipient(IVoiceObserver observer){
            mObserver=observer;
        }
        public void binderDied(){
            _detachVoiceObserver(mObserver);
            mObserver=null;
        }

    }
    private class TextObserverDeathRecipient implements IBinder.DeathRecipient{
        private ITextObserver observer;
        private int pid;
        public TextObserverDeathRecipient(ITextObserver observer,int pid){
            this.observer=observer;
            this.pid=pid;
        }
        public void binderDied(){
            _detachTextObserver(pid,observer);
            observer=null;
        }
    }

    public boolean _attachVoiceObserver(IVoiceObserver observer)
            throws RemoteException{
        synchronized (mObservers){
            IBinder b=observer.asBinder();
            if(!mObservers.containsKey(b.toString())) {
                b.linkToDeath(new VoiceObserverDeathRecipient(observer),0);
                mObservers.put(observer.toString(), observer);
            }
            return true;
        }
    }

    public boolean  _detachVoiceObserver(IVoiceObserver observer){
        synchronized (mObservers) {
            mObservers.remove(observer.toString());
            return  true;
        }
    }

    public boolean _detachTextObserver(int pid, ITextObserver observer){
        synchronized (mTextObservers){
            mTextObservers.remove(pid);
        }
        return true;
    }
    public static ServiceProcessor getInstance(Context context){
        if(instance == null){
            instance=new ServiceProcessor(context);
        }
        return instance;
    }


    public boolean attachVoiceObserver(IVoiceObserver var1) throws RemoteException{
        return _attachVoiceObserver(var1);
    }


    public boolean detachVoiceObserver(IVoiceObserver var1) throws RemoteException{
        return _detachVoiceObserver(var1);
    }


    public boolean requestMicRequest(int var1, String var2, IBinder var3) throws RemoteException{
        return true;
    }


    public boolean requestMic(int var1, String var2, IBinder var3) throws RemoteException{
        return vuiEngine.requestMic(var1,var2,var3);
    }


    public boolean releaseMic(int var1, String var2, IBinder var3) throws RemoteException{
        return vuiEngine.releaseMic(var1,var2,var3);
    }

    public void dismissVuiView() throws RemoteException{

    }


    public List<SpeakerRole> getAllSpeakerRoles() throws RemoteException{
        return vuiEngine.getAllSpeakerRoles();
    }


    public boolean setSpeakerID(int var1) throws RemoteException{
        return vuiEngine.setSpeakerID(var1);
    }

    public int getSpeakerID() throws RemoteException{
        return vuiEngine.getSpeakerID();
    }

    public boolean resetWakeUpSource() throws RemoteException{
        return vuiEngine.resetWakeUpSource();
    }


    public boolean attachTextObserver(int pid, ITextObserver observer) throws RemoteException{
        return  true;
    }


    public boolean detachTextObserver(int pid, ITextObserver observer) throws RemoteException{
        return true;
    }

    public boolean getStringFromVoice(int pid) throws RemoteException{
        return true;
    }
    private boolean allVoiceObserverInvoke(String methodName, Object... typesandArgs){
        int len = typesandArgs.length;
        if(len%2 != 0){
            return  false;
        }
        Method method= null;
        Class<?>[] par=new Class<?>[len/2];
        Object[] args=new Object[len/2];
        try {
            for (int i = 0; 1 < len; i++) {
                par[i] = (Class<?>) typesandArgs[i];
                args[i] = typesandArgs[len / 2 + i];
            }
            method = IVoiceObserver.class.getMethod(methodName, par);

            synchronized (mObservers) {
                Iterator<Map.Entry<String, IVoiceObserver>> it = mObservers.entrySet().iterator();
                while (it.hasNext()) {
                    IVoiceObserver observer = it.next().getValue();
                    if (observer != null) {
                        method.invoke(observer, args);
                    }
                }
            }
        } catch ( Exception e){
            e.printStackTrace();
        }
        return  true;
    }


}
