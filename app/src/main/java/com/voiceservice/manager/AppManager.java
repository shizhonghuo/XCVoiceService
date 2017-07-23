package com.voiceservice.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

import ecarx.app.TBoxManager;
import ecarx.content.IntentHelper;
import ecarx.voiceservice.VoiceSvrManager;
import ecarx.voiceservice.scene.QMusicResult;

/**
 * Created by Administrator on 2017/7/14.
 */

public class AppManager {
    private static final String TAG ="AppManager";
    private static volatile AppManager instance;

    private Context context;

    private AppManager(Context context){
        this.context =context;
    }
    public static AppManager getInstance(Context context){
        synchronized (instance){
            if(instance == null){
                instance =new AppManager(context);
            }
            return instance;
        }
    }

    /* 检查xuyao 需要启动的APP 是否安装
    *  GET_SIGNATURES =0
    */
    private boolean isAppInstall(String packageName){
        final PackageManager packageManager=context.getPackageManager();
        List<PackageInfo>  infos=packageManager.getInstalledPackages(0);
        if(infos.size()>0){
            for(int i=0;i<infos.size(); i++){
                if(packageName.equals(infos.get(i))){
                    return true;
                }
            }
        }
        return false;
    }

    private void startAppByCpm(String packageName,String activityName){
        if(!isAppInstall(packageName)){
            return ;
        }
        Intent intent= new Intent();
        intent.setComponent(new ComponentName(packageName,activityName));
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void stopAppBybroadcast(String appType){
        Intent intent= new Intent(VoiceSvrManager.BROADCAST_VOICE_EXIT_APP);
        intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE,appType);
    }

    public boolean launchRadio(boolean action){
        Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MEDIA);
        intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, VoiceSvrManager.APP_TYPE_RADIO);
        intent.putExtra(VoiceSvrManager.EXTAR_ACTION_TYPE,
                action ? VoiceSvrManager.DO_ACTION_TYPE_LAUNCH : VoiceSvrManager.DO_ACTION_TYPE_EXIT);
        intent.putExtra(VoiceSvrManager.EXTAR_SOURCE_TYPE, VoiceSvrManager.SOURCE_TYPE_LOCAL);
        context.sendBroadcast(intent);
        return true;
    }

    public boolean launchOnlineRadio(boolean action) {

        Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MEDIA);
        intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, VoiceSvrManager.APP_TYPE_RADIO);
        intent.putExtra(VoiceSvrManager.EXTAR_ACTION_TYPE,
                action ? VoiceSvrManager.DO_ACTION_TYPE_LAUNCH : VoiceSvrManager.DO_ACTION_TYPE_EXIT);
        intent.putExtra(VoiceSvrManager.EXTAR_SOURCE_TYPE, VoiceSvrManager.SOURCE_TYPE_ONLINE);
        context.sendBroadcast(intent);
        return true;
    }


    public boolean launchMusic(boolean action) {
        if(action){
            Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MUSIC);
            intent.putExtra(VoiceSvrManager.EXTRA_CONTROL_BEAN, new QMusicResult());
            context.sendBroadcast(intent);
        }else{
            Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MEDIA);
            intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, VoiceSvrManager.APP_TYPE_MUSIC);
            intent.putExtra(VoiceSvrManager.EXTAR_ACTION_TYPE, VoiceSvrManager.DO_ACTION_TYPE_EXIT);
            context.sendBroadcast(intent);
        }
        return true;
    }

    public boolean launchBTMusic(boolean action) {

        Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MEDIA);
        intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, VoiceSvrManager.APP_TYPE_MUSIC);
        intent.putExtra(VoiceSvrManager.EXTAR_ACTION_TYPE,
                action ? VoiceSvrManager.DO_ACTION_TYPE_LAUNCH : VoiceSvrManager.DO_ACTION_TYPE_EXIT);
        intent.putExtra(VoiceSvrManager.EXTAR_SOURCE_TYPE, VoiceSvrManager.SOURCE_TYPE_BLUETOOTH);
        context.sendBroadcast(intent);
        return true;
    }

    public boolean launchUSBMusic(boolean action) {

        Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MEDIA);
        intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, VoiceSvrManager.APP_TYPE_MUSIC);
        intent.putExtra(VoiceSvrManager.EXTAR_ACTION_TYPE,
                action ? VoiceSvrManager.DO_ACTION_TYPE_LAUNCH : VoiceSvrManager.DO_ACTION_TYPE_EXIT);
        intent.putExtra(VoiceSvrManager.EXTAR_SOURCE_TYPE, VoiceSvrManager.SOURCE_TYPE_UDISK);
        context.sendBroadcast(intent);
        return true;
    }

    public boolean launchOnlineMusic(boolean action) {

        Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_CONTROL_MEDIA);
        intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, VoiceSvrManager.APP_TYPE_MUSIC);
        intent.putExtra(VoiceSvrManager.EXTAR_ACTION_TYPE,
                action ? VoiceSvrManager.DO_ACTION_TYPE_LAUNCH : VoiceSvrManager.DO_ACTION_TYPE_EXIT);
        intent.putExtra(VoiceSvrManager.EXTAR_SOURCE_TYPE, VoiceSvrManager.SOURCE_TYPE_ONLINE);
        context.sendBroadcast(intent);
        return true;
    }

    public boolean lauchNews(boolean action){
        if(action){
            startAppByCpm(IntentHelper.ACTION_APP_NEWS_PACKAGE_NAME,
                    IntentHelper.ACTION_APP_NEWS_MAIN_CLASS_NAME);
        } else {
            stopAppBybroadcast(IntentHelper.APP_TYPE_NEWS);
        }
        return true;
    }

    public boolean lauchVideo(boolean action){
        if(action){
            startAppByCpm(IntentHelper.ACTION_APP_VIDEO_PACKAGE_NAME,
                    IntentHelper.ACTION_APP_VIDEO_MAIN_CLASS_NAME);
        } else {
            stopAppBybroadcast(IntentHelper.APP_TYPE_VIDEO);
        }
        return true;
    }

    public boolean launchCarPlay(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_CARPLAY_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_CARPLAY_MAIN_CLASS_NAME);
            } else {
                //stopAppByBroadcast(IntentHelper.APP_TYPE_CARPLAY);
                return false;
            }

        return true;
    }

    public boolean launchCarLife(boolean action) {

            if (action) {
                startAppByCpm("com.ecarx.carlife.ui",
                        "com.ecarx.carlife.ui.ActSplash");
            } else {
                //stopAppByBroadcast(IntentHelper.APP_TYPE_CARLIFE);
                return false;
            }
        return true;
    }

    public boolean launchDVR(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_DVR_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_DVR_MAIN_CLASS_NAME);
            } else {
                //stopAppByBroadcast(IntentHelper.APP_TYPE_DVR);
                return false;
            }
        return true;
    }
    public boolean launchBCM(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_VEHICLE_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_VEHICLE_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_VEHICLE_SETTINGS);
            }
        return true;
    }

    public boolean launchAux(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_AUX_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_AUX_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_AUX);
            }

        return true;
    }

    public boolean launchBTCategory(boolean action, String category) {

            if (IntentHelper.CONTACT_CATEGORY_CONTACT.equals(category)
                    || IntentHelper.CONTACT_CATEGORY_CALLLOG.equals(category)
                    || IntentHelper.CONTACT_CATEGORY_DIAL.equals(category)) {
                if (action) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(IntentHelper.ACTION_APP_CONTACT_PACKAGE_NAME,
                            IntentHelper.ACTION_APP_CONTACT_MAIN_CLASS_NAME));
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(IntentHelper.EXTRA_CONTACT_CATEGORY, category);
                    context.startActivity(intent);
                    return true;
                } else {
                    Intent intent = new Intent(VoiceSvrManager.BROADCAST_VOICE_EXIT_APP);
                    intent.putExtra(VoiceSvrManager.EXTAR_APP_TYPE, IntentHelper.APP_TYPE_CONTACT);
                    intent.putExtra(IntentHelper.EXTRA_CONTACT_CATEGORY, category);
                    context.sendBroadcast(intent);
                }
            }
        return false;
    }

    public boolean launchNavi(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_NAVI_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_NAVI_MAIN_CLASS_NAME);
            } else {
                Intent intent = new Intent(IntentHelper.ACTION_EXIT_AMAPAUTO);
                intent.putExtra(IntentHelper.EXTRA_AMAPAUTO_ACTION, IntentHelper.EXTRA_AMAPAUTO_VALUE);
                context.sendBroadcast(intent);
            }
        return true;
    }

    public boolean launchSettings(boolean action) {

            if (action) {
                final Intent intent = new Intent();
                intent.setAction(IntentHelper.ACTION_APP_SETTINGS_ACTIONG);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else stopAppBybroadcast(IntentHelper.APP_TYPE_SETTINGS);
        return true;
    }

    public boolean launchPhoto(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_PHOTO_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_PHOTO_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_PHOTO);
            }
        return true;
    }

    public boolean launchXCall(boolean action, int xCallType) {

            if (TBoxManager.CALL_TYPE_B_I_CALL == xCallType
                    || TBoxManager.CALL_TYPE_ECALL == xCallType) {
                if (action) {
                    final Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName(IntentHelper.ACTION_APP_XCALL_PACKAGE_NAME,
                            IntentHelper.ACTION_APP_XCALL_MAIN_CLASS_NAME);
                    intent.putExtra(IntentHelper.EXTRA_XCALL_TYPE, xCallType);
                    context.startActivity(intent);
                    return true;
                }
            }
        return false;
    }

    public boolean launchWeather(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_WEATHER_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_WEATHER_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_WEATHER);
            }
        return true;
    }

    public boolean launchInBox(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_INBOX_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_INBOX_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_INBOX);
            }
        return true;
    }

    public boolean launch360Camera(boolean action) {

            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_CAMERA360_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_CAMERA360_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_CAMERA360);
            }
        return true;
    }

    public boolean launchExplorer(boolean action) {
            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_FILE_EXPLORER_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_FILE_EXPLORER_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_FILE_EXPLORER);
            }
        return true;
    }

    public boolean launchViolation(boolean action) {
            if (action) {
                startAppByCpm(IntentHelper.ACTION_APP_VIOLATION_PACKAGE_NAME,
                        IntentHelper.ACTION_APP_VIOLATION_MAIN_CLASS_NAME);
            } else {
                stopAppBybroadcast(IntentHelper.APP_TYPE_VIOLATION);
            }
        return true;
    }




}
