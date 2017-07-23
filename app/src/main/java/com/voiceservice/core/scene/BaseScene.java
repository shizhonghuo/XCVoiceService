package com.voiceservice.core.scene;

import android.content.Context;
import android.os.Handler;

/**
 * Created by Administrator on 2017/7/14.
 */

public abstract class BaseScene {
    protected  static final String JSON_TAG_KEY_FOCUS = "foocus";
    protected static final String JSON_TAG_KEY_RAWTEXT="rawtext";
    protected static final String JSON_FOCUS_TAG_MUSIC="music";
    protected  static final String JSON_FOCUS_TAG_RADIO="radio";
    protected static final String JSON_FOCUS_TAG_CMD="cmd";
    protected static final String JSON_FOCUS_TAG_APP="APP";
    protected static final String JSON_FOCUS_TAG_AIRCONTROL="airControl";

    public static final int HANDLER_OK=0;
    public static final int HANDLER_ERROR=1;
    public static final int HANDLER_NO_HANDLE=2;

    /**
     * 未知场景
     */
    public static final Integer SCENE_TYPE_UNKNOWN = -1;
    public static final Integer SCENE_TYPE_MUSIC = 0;
    public static final Integer SCENE_TYPE_RADIO = 1;
    public static final Integer SCENE_TYPE_CMD = 2;
    public static final Integer SCENE_TYPE_APP = 3;
    public static final Integer SCENE_TYPE_AIRCONTROL = 4;
    public static final Integer SCENE_TYPE_CAR_CONTROL = 5;
    public static final Integer SCENE_TYPE_VIOLATION = 6;
    protected Context context;
    protected int mSceneType = SCENE_TYPE_UNKNOWN;
    protected Handler handler;

    public void BaseScene(Context context,Handler handler){
        this.context=context;
        this.handler=handler;
    }

    public abstract int handleScene(String actionJson);
}
