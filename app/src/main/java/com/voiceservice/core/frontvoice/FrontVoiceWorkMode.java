package com.voiceservice.core.frontvoice;

/**
 * Created by Administrator on 2017/7/20.
 */

public class FrontVoiceWorkMode {
    // 录音模式
    public static final int WORK_MODE_PASSBY = 0;

    // 降噪模式
    public static final int WORK_MODE_NOISECLEAN = 1;

    // 回声消除模式
    public static final int WORK_MODE_ECHO_CANCEL = 2;

    // 唤醒模式
    public static final int WORK_MODE_VOICE_WAKEUP = 3;

    public static final int WORK_MODE_DEFAULT = WORK_MODE_VOICE_WAKEUP;
}
