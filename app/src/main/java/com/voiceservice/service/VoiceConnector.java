package com.voiceservice.service;

import android.os.IBinder;
import android.os.RemoteException;

import java.util.List;

import ecarx.voiceservice.ITextObserver;
import ecarx.voiceservice.IVoiceObserver;
import ecarx.voiceservice.IVoiceService;
import ecarx.voiceservice.role.SpeakerRole;

/**
 * Created by Administrator on 2017/7/13.
 */

public class VoiceConnector extends IVoiceService.Stub {
    private static VoiceConnector connector;
    private ServiceProcessor processor;

    private VoiceConnector(ServiceProcessor processor){
        this.processor=processor;
    }

    public static VoiceConnector getConnector(ServiceProcessor processor){
        if(connector ==null){
            connector=new VoiceConnector(processor);
        }
        return connector;
    }


    public boolean isConnected() throws RemoteException{
        return processor.isConnected();
    }

    public boolean attachVoiceObserver(IVoiceObserver var1) throws RemoteException{
        return processor.attachVoiceObserver(var1);
    }


    public boolean detachVoiceObserver(IVoiceObserver var1) throws RemoteException{
        return processor.detachVoiceObserver(var1);
    }


    public boolean requestMicRequest(int var1, String var2, IBinder var3) throws RemoteException{
        return processor.requestMicRequest(var1,var2,var3);
    }


    public boolean requestMic(int var1, String var2, IBinder var3) throws RemoteException{
        return processor.requestMic(var1,var2,var3);
    }


    public boolean releaseMic(int var1, String var2, IBinder var3) throws RemoteException{
        return processor.releaseMic(var1,var2,var3);
    }

    public void dismissVuiView() throws RemoteException{
        processor.dismissVuiView();
    }


    public List<SpeakerRole> getAllSpeakerRoles() throws RemoteException{
        return processor.getAllSpeakerRoles();
    }


    public boolean setSpeakerID(int var1) throws RemoteException{
        return processor.setSpeakerID(var1);
    }

    public int getSpeakerID() throws RemoteException{
        return processor.getSpeakerID();
    }

    public boolean resetWakeUpSource() throws RemoteException{
        return processor.resetWakeUpSource();
    }


    public boolean attachTextObserver(int pid, ITextObserver var2) throws RemoteException {
        return processor.attachTextObserver(pid, var2);
    }

    public boolean detachTextObserver(int pid, ITextObserver var2) throws RemoteException{
        return processor.detachTextObserver(pid,var2);
    }

    public boolean getStringFromVoice(int pid) throws RemoteException{
        return processor.getStringFromVoice(pid);
    }
}
