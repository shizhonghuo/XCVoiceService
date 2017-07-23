package com.voiceservice.core.scene;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.voiceservice.service.ServiceProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import ecarx.voiceservice.scene.QMusicResult;

/**
 * Created by Administrator on 2017/7/21.
 * music scene 信息比较复杂， 调用ServiceProcessor 的IVoiceObserver 回调处理。
 */

public class MusicScene extends BaseScene {
    private static final String TAG= "MusicScene";
    private static final String JSON_OPERATION="operation";
    private static final String JSON_SONG="song";
    private static final String JSON_ARTIST="album";
    private static final String JSON_ALBUM="album";
    private static final String JSON_CATEGORY="category";
    private static final String JSON_SOURCE="SOURCE";

    public MusicScene(Context context, Handler handler){
        super.BaseScene(context,handler);
        mSceneType=BaseScene.SCENE_TYPE_MUSIC;
    }

    @Override
    public int handleScene(String actionJson) {
        try {
            JSONObject action = new JSONObject(actionJson);
            // 只处理Music的
            if (BaseScene.JSON_FOCUS_TAG_MUSIC.equals(action.getString(BaseScene.JSON_TAG_KEY_FOCUS))) {
                String operator = "";
                if (action.has(JSON_OPERATION)) {
                    operator = action.getString(JSON_OPERATION);
                }
                String rawText = "";
                if (action.has(JSON_TAG_KEY_RAWTEXT)) {
                    rawText = action.getString(JSON_TAG_KEY_RAWTEXT);
                }
                String artist = "";
                if (action.has(JSON_ARTIST)) {
                    artist = action.getString(JSON_ARTIST);
                }
                String song = "";
                if (action.has(JSON_SONG)) {
                    song = action.getString(JSON_SONG);
                }
                String category = "";
                if (action.has(JSON_CATEGORY)) {
                    category = action.getString(JSON_CATEGORY);
                }
                String album = "";
                if (action.has(JSON_ALBUM)) {
                    album = action.getString(JSON_ALBUM);
                }
                String sourceType = "";
                if (action.has(JSON_SOURCE)) {
                    sourceType = action.getString(JSON_SOURCE);
                }
                QMusicResult musicResult = new QMusicResult();
                if (!TextUtils.isEmpty(operator)) {
                    if ("PLAY".equals(operator.toUpperCase())) {
                        musicResult.operation = QMusicResult.OPERATION_PLAY;
                    } else if ("SEARCH".equals(operator.toUpperCase())) {
                        musicResult.operation = QMusicResult.OPERATION_SEARCH;
                    } else if ("CLOSE".equals(operator.toUpperCase())) {
                        musicResult.operation = QMusicResult.OPERATION_CLOSE;
                    }
                } else {
                    musicResult.operation = QMusicResult.OPERATION_PLAY;
                }

                musicResult.rawText = rawText;
                musicResult.song = song;
                musicResult.artist = artist;
                musicResult.album = album;

                if ("抒情".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_LYRIC;
                } else if ("古典".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_CLASSICAL;
                } else if ("流行".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_POPULAR;
                } else if ("蓝调".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_BLUES;
                } else if ("乡村".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_COUNTRY;
                } else if ("校园".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_CAMPUS;
                } else if ("嘻哈".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_HIP_POP;
                } else if ("摇滚".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_ROCK;
                } else if ("爵士".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_JAZZ;
                } else if ("轻音乐".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_LIGHT_MUSIC;
                } else if ("经典".equals(category)) {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_CLASSIC;
                } else {
                    musicResult.category = QMusicResult.CATEGRORY_TYPE_UNKNOWN;
                }

                if ("U盘".equalsIgnoreCase(sourceType) || "U盘音乐".equals(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_UDISK;
                } else if ("iPod".equalsIgnoreCase(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_IPOD;
                } else if ("SD".equalsIgnoreCase(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_SD;
                } else if ("CD".equals(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_CD;
                } else if ("本地".equals(sourceType) || "本地音乐".equals(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_LOCAL;
                } else if ("网络".equals(sourceType) || "网络音乐".equals(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_INTERNET;
                } else if ("蓝牙".equals(sourceType) || "蓝牙音乐".equals(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_BLUETOOTH;
                } else if ("最爱音乐".equals(sourceType)) {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_PRIVATE;
                } else {
                    musicResult.sourceType = QMusicResult.SOURCE_TYPE_UNKNOWN;
                }


                Message msg =handler.obtainMessage(ServiceProcessor.MSG_CALLBACK_MUSIC_SCENE);
                msg.obj = musicResult;
                handler.sendMessage(msg);
                return BaseScene.HANDLER_OK;
            }
        } catch (JSONException e) {

            return BaseScene.HANDLER_ERROR;
        }
        return BaseScene.HANDLER_NO_HANDLE;
    }
}

