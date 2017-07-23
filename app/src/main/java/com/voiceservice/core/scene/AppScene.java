package com.voiceservice.core.scene;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.voiceservice.manager.AppManager;
import com.voiceservice.manager.SettingManager;
import com.voiceservice.service.ServiceProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import ecarx.content.IntentHelper;

/**
 * Created by Administrator on 2017/7/14.
 */

public class AppScene extends BaseScene {

    private static final String Tag="AppScene";
    private static final String JSON_NAME ="name";
    private static final String JSON_OPERATION="operation";

    private static final String APP_NAME_NAVI = "导航";
    private static final String APP_NAME_RADIO = "收音机";
    private static final String APP_NAME_MUSIC = "音乐";
    private static final String APP_NAME_MUSIC2 = "多媒体";
    private static final String APP_NAME_FAVORITE_MUSIC = "最爱音乐";
    private static final String APP_NAME_USB_MUSIC1 = "U盘音乐";
    private static final String APP_NAME_USB_MUSIC2 = "优盘音乐";
    private static final String APP_NAME_BT_MUSIC = "蓝牙音乐";
    private static final String APP_NAME_ONLINE_MUSIC1 = "在线音乐";
    private static final String APP_NAME_ONLINE_MUSIC2 = "网络音乐";
    private static final String APP_NAME_ONLINE_MUSIC3 = "慧听";
    private static final String APP_NAME_ONLINE_RADIO = "在线电台";
    private static final String APP_NAME_ONLINE_STORY = "在线听书";
    private static final String APP_NAME_ONLINE_STORY2 = "听书";
    private static final String APP_NAME_CONTACTS = "通讯录";
    private static final String APP_NAME_PHONE = "电话";
    private static final String APP_NAME_BT_PHONE = "蓝牙电话";
    private static final String APP_NAME_BT = "蓝牙";
    private static final String APP_NAME_WIFI = "WIFI";
    private static final String APP_NAME_BT_CALLLOG = "通话记录";
    private static final String APP_NAME_WEATHER = "天气";
    private static final String APP_NAME_AUX = "AUX";
    private static final String APP_NAME_VEHICLE = "车辆";
    private static final String APP_NAME_VEHICLE_SETTING = "车辆设置";
    private static final String APP_NAME_360 = "泊车影像";
    private static final String APP_NAME_360_1 = "环视";
    private static final String APP_NAME_360_2 = "360影像";
    private static final String APP_NAME_DVR = "行车记录仪";
    private static final String APP_NAME_DVR2 = "行车记录";
    private static final String APP_NAME_VIDEOS = "视频";
    private static final String APP_NAME_INBOX = "收件箱";
    private static final String APP_NAME_NEWS = "新闻";
    private static final String APP_NAME_ONLINE_NEWS = "在线新闻";
    private static final String APP_NAME_SETTINGS = "设置";
    private static final String APP_NAME_FILE_EXP = "文件管理";
    private static final String APP_NAME_FILE_EXP2 = "文件管理器";
    private static final String APP_NAME_CARPLAY = "CarPlay";
    private static final String APP_NAME_CARLIFE = "CarLife";
    private static final String APP_NAME_MY_ORDER = "系统信息";
    private static final String APP_NAME_MESSAGE_BOX = "消息盒子";
    private static final String APP_NAME_VIOLATION = "违章查询";
    private static final String APP_NAME_BCALL = "道路救援";
    private static final String APP_NAME_ECALL = "紧急救援";
    private static final String APP_NAME_ECALL2 = "紧急呼叫";
    private static final String APP_NAME_ICALL = "行驶助手";
    private static final String APP_NAME_GUIDE = "使用说明";
    private static final String APP_NAME_PHOTO1 = "图库";
    private static final String APP_NAME_PHOTO2 = "图片";
    private static final String APP_NAME_AIR_UI1 = "空调";
    private static final String APP_NAME_AIR_UI2 = "空调界面";
    private static final String APP_NAME_GSTORE = "GStore";
    private static final String APP_NAME_INSURANCE = "碰瓷险";

    private AppManager mAppManager;
    private SettingManager mSettingMng;

    public AppScene (Context context, Handler handler){
        super.BaseScene(context,handler);
        mSceneType=BaseScene.SCENE_TYPE_APP;
        mAppManager=AppManager.getInstance(context);
        mSettingMng=SettingManager.getInstance(context);
    }


    public  int handleScene(String actionJson){
        try {
            JSONObject action = new JSONObject(actionJson);
            if(BaseScene.JSON_FOCUS_TAG_APP.equals(action.getString(BaseScene.JSON_TAG_KEY_FOCUS))){
                String rawText = "";
                if(action.has(BaseScene.JSON_TAG_KEY_RAWTEXT)){
                    rawText=action.getString(BaseScene.JSON_TAG_KEY_RAWTEXT);
                }
                String name = "";
                if (action.has(JSON_NAME)) {
                    name = action.getString(JSON_NAME);
                }
                String operation = "";
                if (action.has(JSON_OPERATION)) {
                    operation = action.getString(JSON_OPERATION);
                }
                if(TextUtils.isEmpty(name)){
                    return BaseScene.HANDLER_ERROR;
                }

                boolean flag= false;

                if( TextUtils.isEmpty(operation) || "LAUNCH".equals(operation.toUpperCase())){
                    flag=doProcessAppAction(name, true);
                } else if("EXIT".equals(operation.toUpperCase())){
                    flag=doProcessAppAction(operation,false);
                }
                if( !flag){
                    Message msg= handler.obtainMessage(ServiceProcessor.MSG_SPEAKER_HINT_FOR_NO_SUPPORT);
                    handler.sendMessageDelayed(msg,300);
                }
                return flag ? BaseScene.HANDLER_OK:BaseScene.HANDLER_NO_HANDLE;
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return 0;
    }


    private boolean doProcessAppAction(String name, boolean action){
        boolean ret = false;
        if(APP_NAME_MUSIC.equals(name.toUpperCase())
                ||APP_NAME_MUSIC2.equals(name.toUpperCase())
                ||APP_NAME_FAVORITE_MUSIC.equals(name.toUpperCase())){
            ret=mAppManager.launchMusic(action);
        }
        // U盘音乐
        else if (APP_NAME_USB_MUSIC1.equals(name.toUpperCase())
                || APP_NAME_USB_MUSIC2.equals(name.toUpperCase())) {
            ret = mAppManager.launchUSBMusic(action);
        }
        // 蓝牙音乐
        else if (APP_NAME_BT_MUSIC.equals(name.toUpperCase())) {
            ret = mAppManager.launchBTMusic(action);
        }
        // 收音机
        else if (APP_NAME_RADIO.equals(name.toUpperCase())) {
            ret = mAppManager.launchRadio(action);
        }
        // Online Music
        else if (APP_NAME_ONLINE_MUSIC1.equals(name.toUpperCase())
                || APP_NAME_ONLINE_MUSIC2.equals(name.toUpperCase())
                || APP_NAME_ONLINE_MUSIC3.equals(name.toUpperCase())) {
            ret = mAppManager.launchOnlineMusic(action);
        }
        // Online Radio
        else if (APP_NAME_ONLINE_RADIO.equals(name.toUpperCase())
                || APP_NAME_ONLINE_STORY.equals(name.toUpperCase())
                || APP_NAME_ONLINE_STORY2.equals(name.toUpperCase())) {
            ret = mAppManager.launchOnlineRadio(action);
        }
        // 导航
        else if (APP_NAME_NAVI.equals(name.toUpperCase())) {
            ret = mAppManager.launchNavi(action);
        }
        // Settings
        else if (APP_NAME_SETTINGS.equals(name.toUpperCase())) {
            ret = mAppManager.launchSettings(action);
        }
        // Contacts
        else if (APP_NAME_CONTACTS.equals(name.toUpperCase())) {
            ret = mAppManager.launchBTCategory(action, IntentHelper.CONTACT_CATEGORY_CONTACT);
        }
        // Phone
        else if (APP_NAME_PHONE.equals(name.toUpperCase())
                || APP_NAME_BT_PHONE.equals(name.toUpperCase())) {
            ret = mAppManager.launchBTCategory(action, IntentHelper.CONTACT_CATEGORY_DIAL);
        }
        else if(APP_NAME_BT.equals(name.toUpperCase())){
            ret=mSettingMng.switchBT(action);
        }
        // Wi-Fi
        else if (APP_NAME_WIFI.equals(name.toUpperCase())) {
            ret = mSettingMng.switchWifi(action);
        }
        // CALLLOG
        else if (APP_NAME_BT_CALLLOG.equals(name.toUpperCase())) {
            ret = mAppManager.launchBTCategory(action, IntentHelper.CONTACT_CATEGORY_CALLLOG);
        }
        // Weather
        else if (APP_NAME_WEATHER.equals(name.toUpperCase())) {
            ret =mAppManager.launchWeather(action);
        }
        // Aux
        else if (APP_NAME_AUX.equals(name.toUpperCase())) {
            ret = mAppManager.launchAux(action);
        }
        return ret;
    }

}