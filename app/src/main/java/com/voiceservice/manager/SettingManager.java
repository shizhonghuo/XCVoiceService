package com.voiceservice.manager;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by Administrator on 2017/7/20.
 */

public class SettingManager {
    private static final String TAG="SettingManager";
    private Context mContext;
    private static volatile  SettingManager mInstance;
    private WifiManager mWm;

    private SettingManager (Context context){
        mContext=context;
    }

    public static SettingManager getInstance(Context context){
        if(mInstance == null){
            synchronized (SettingManager.class){
                mInstance=new SettingManager(context);
            }
        }
        return  mInstance;
    }
    public boolean switchBT(boolean state){
        if( BluetoothMng.getsInstance(mContext) != null){
            BluetoothMng.getsInstance(mContext).setBTEnable(state);
            return true;
        }
        return  false;
    }

    public boolean switchWifi(boolean state){
        if( mWm == null){
            mWm =(WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }
        if(state){
            if( !mWm.isWifiEnabled()){
                mWm.setWifiEnabled(true);
            } else {
                if( mWm.isWifiEnabled()){
                    mWm.setWifiEnabled(false);
                }
            }
        }
        return true;
    }

}
