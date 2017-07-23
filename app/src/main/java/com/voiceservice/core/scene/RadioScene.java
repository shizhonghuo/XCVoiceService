package com.voiceservice.core.scene;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.voiceservice.common.Util;
import com.voiceservice.service.ServiceProcessor;

import org.json.JSONException;
import org.json.JSONObject;


import ecarx.voiceservice.scene.QRadioResult;

/**
 * Created by Administrator on 2017/7/21.
 */

public class RadioScene extends BaseScene {

    private static final String TAG = "RadioScene";
    private static final String JSON_NAME = "name";
    private static final String JSON_CODE = "code";
    private static final String JSON_WAVEBAND = "waveband";
    private static final String JSON_CATEGORY = "category";
    private static final String JSON_LOCATION = "location";

    public RadioScene(Context context, Handler handler) {
        super.BaseScene(context, handler);
        mSceneType = BaseScene.SCENE_TYPE_RADIO;
    }

    @Override
    public int handleScene(String actionJson) {
        try {
            JSONObject action = new JSONObject(actionJson);
            // 只处理Radio的
            if (BaseScene.JSON_FOCUS_TAG_RADIO.equals(action.getString(BaseScene.JSON_TAG_KEY_FOCUS))) {
                String rawText = "";
                if(action.has(JSON_TAG_KEY_RAWTEXT)){
                    rawText = action.getString(JSON_TAG_KEY_RAWTEXT);
                }
                String name = "";
                if(action.has(JSON_NAME)){
                    name = action.getString(JSON_NAME);
                }
                String code = "";
                if(action.has(JSON_CODE)){
                    code = action.getString(JSON_CODE);
                }
                String waveband = "";
                if(action.has(JSON_WAVEBAND)){
                    waveband = action.getString(JSON_WAVEBAND);
                }
                String category = "";
                if(action.has(JSON_CATEGORY)){
                    category = action.getString(JSON_CATEGORY);
                }
                String location = "";
                if(action.has(JSON_LOCATION)){
                    location = action.getString(JSON_LOCATION);
                }
                QRadioResult radioResult = new QRadioResult();
                radioResult.name = name;
                radioResult.rawText = rawText;
                if (!TextUtils.isEmpty(code) && Util.isNum(code)) {
                    radioResult.freq = code;
                }
                if ("fm".equals(waveband)) {
                    radioResult.bandType = QRadioResult.BAND_TYPE_FM;
                } else if ("am".equals(waveband)) {
                    radioResult.bandType = QRadioResult.BAND_TYPE_AM;
                } else {
                    radioResult.bandType = QRadioResult.BAND_TYPE_UNKNOWN;
                }
                radioResult.location = location;
                radioResult.category = category;
                Message msg = handler.obtainMessage(ServiceProcessor.MSG_CALLBACK_RADIO_SCENE);
                msg.obj = radioResult;
                handler.sendMessage(msg);
                return BaseScene.HANDLER_OK;
            }
        } catch (JSONException e) {

            return BaseScene.HANDLER_ERROR;
        }
        return BaseScene.HANDLER_NO_HANDLE;
    }
}
