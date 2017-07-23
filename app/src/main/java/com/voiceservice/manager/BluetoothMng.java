package com.voiceservice.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

import ecarx.bluetooth.BluetoothAdapterCreateHelper;
import ecarx.bluetooth.ECarXBluetoothCarHeadset;

/**
 * Created by Administrator on 2017/7/14.
 */

public class BluetoothMng {
    private static final String TAG ="BluetoolMng";
    private static volatile BluetoothMng sInstance =null;
    private Context mContext;
    private BluetoothAdapter mBtAdapter;
    private ECarXBluetoothCarHeadset ecarXHFP;
    private IBTPhoneStateChange iCallback;
    private int btState = -1;

    public interface IBTPhoneStateChange {
        public void onPhoneStateChanged(int state, String number);
        public void onHFPStateChanged(boolean connected);
    }

    private BluetoothMng(Context context){
        this.mContext=context;
        BluetoothAdapterCreateHelper.getDefaultAdapter(mContext,mInitiaListener);
    }

    public static BluetoothMng getsInstance(Context context){
        if (sInstance == null) {
            synchronized (BluetoothMng.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothMng(context);
                }
            }
        }
        return sInstance;
    }

    public void setBTPhoneStateChange(IBTPhoneStateChange iCallback){
        this.iCallback=iCallback;
    }


    private BluetoothAdapterCreateHelper.AdapterListener mInitiaListener =new BluetoothAdapterCreateHelper.AdapterListener() {
        @Override
        public void onAdapterCreate() {
            InitialBluetooth();
        }
    };

    public void InitialBluetooth(){
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        ecarXHFP=ECarXBluetoothCarHeadset.getInstance(mContext);
        if(ecarXHFP.isAudioOn()){
            setBTPhoneState(TelephonyManager.CALL_STATE_OFFHOOK, null);
        } else {
            setBTPhoneState(TelephonyManager.CALL_STATE_IDLE,null);
        }
        ecarXHFP.registerEventListener(mHeadsetEventListener);
    }

    private void setBTPhoneState(int state,String number){
        if(btState != state){
            btState=state;
            if(iCallback != null){
                iCallback.onPhoneStateChanged(state,number);
            }
        }
    }

    public boolean setBTEnable(boolean on){
        boolean ret =false;
        if(mBtAdapter != null){
            if(on){
                if(!isBTEnable()){
                    ret =mBtAdapter.enable();
                } else {
                    if(isBTEnable()){
                        ret=mBtAdapter.disable();
                    }
                }
                return  ret;
            }
        }
        return false;
    }

    public boolean isBTEnable(){
        if(mBtAdapter != null){
            return  mBtAdapter.isEnabled();
        }
        return false;
    }

    public boolean isHFPConnected(){
        if(isBTEnable() && mBtAdapter != null){
            return mBtAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) == BluetoothAdapter.STATE_CONNECTED;
        }
        return false;
    }

    public boolean handlerUserChangePhoneStateByVoice(int state) {
        switch (state) {
            case TelephonyManager.CALL_STATE_OFFHOOK:
                return true;
            case TelephonyManager.CALL_STATE_IDLE:
                return true;
            default:
                return false;
        }
    }
    private ECarXBluetoothCarHeadset.HeadsetEventListener mHeadsetEventListener=
            new ECarXBluetoothCarHeadset.HeadsetEventListener(){

            public void audioEstablished(){
                setBTPhoneState(TelephonyManager.CALL_STATE_OFFHOOK,null);
            }

            public void audioReleased(){
                setBTPhoneState(TelephonyManager.CALL_STATE_IDLE,null);
            }

            public void inComingCall(String number){
                setBTPhoneState(TelephonyManager.CALL_STATE_RINGING,null);
            }

            public void outGoingCall(String number){

            }


            public void onGoingCall(String number){
                setBTPhoneState(TelephonyManager.CALL_STATE_OFFHOOK,null);
            }

             @Override
             public void serviceEstablished() {

                }

                @Override
                public void serviceReleased() {

                }

                public void hangup(String number){
                    setBTPhoneState(TelephonyManager.CALL_STATE_IDLE,null);
                }

                @Override
                public void ringFromPhone(boolean isFromPhone) {
                }

                public void onProfileStateChange(int proFileState){
                    if(proFileState == BluetoothProfile.STATE_DISCONNECTED
                            ||proFileState==BluetoothProfile.STATE_DISCONNECTING){
                        setBTPhoneState(TelephonyManager.CALL_STATE_IDLE,null);
                    } else if(proFileState == BluetoothProfile.STATE_CONNECTED){
                        if(ecarXHFP.isAudioOn()){
                            setBTPhoneState(TelephonyManager.CALL_STATE_OFFHOOK,null);
                        } else {
                            setBTPhoneState(TelephonyManager.CALL_STATE_IDLE,null);
                        }
                    }
                }
            };

    public int getBTPhoneState() {
        return btState;
    }

    //
    public boolean doStartBTCall(String number) {
        if (isHFPConnected()) {
            Uri uri = Uri.fromParts("tel", number, null);
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           // mContext.startActivity(intent);
            return true;
        }
        return true;
    }

}
