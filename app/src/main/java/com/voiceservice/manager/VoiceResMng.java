package com.voiceservice.manager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.speech.tts.Voice;

import com.iflytek.platform.type.PlatformCode;
import com.iflytek.platformservice.PlatformService;
import com.voiceservice.core.frontvoice.DeviceNodeControler;
import com.voiceservice.core.frontvoice.FrontVoiceWorkMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ecarx.voiceservice.VoiceSvrManager;

/**
 * Created by Administrator on 2017/7/20.
 */

public class VoiceResMng {
    private static final String TAG="VoiceResMng";
    private static volatile  VoiceResMng mInstance;
    private Context mContext;
    private DeviceNodeControler mControler= null;
    private Stack<Integer> mNodeStack = null;
    private Handler mNodeHandler = null;
    private HandlerThread mWorker = null;

    private static final int REQUEST_MODE= 0;
    private static final int RELEASE_MODE= 1;
    private static final int UPDATE_WAKEUP_WORK=2;
    //private Map<String, IBinder> mRequestClients = new HashMap<String, IBinder>();
    private static final int MIC_INITIAL= -1;
    private static final int MIC_RELEASE =0;
    private static final int MIC_HOLD = 1;

    /*
    * MIC_CONTROL
     */

    private int mMicPriorityFlag= 0;
    public int[] mPriorityValue=new int[]{
      MIC_RELEASE, //VoiceSvrManager.MIC_REQUEST_TYPE_RECORD
      MIC_RELEASE, //VoiceSvrManager.MIC_REQUEST_TYPE_CALL
      MIC_RELEASE, //VoiceSvrManager.MIC_REQUEST_TYPE_SYSTEM
    };

    /**
     * 标记系统应用请求MIC的状态，如果系统应用希望请求MIC，则不给讯飞
     *
     * @see #MIC_RELEASE
     * @see #MIC_HOLD
     */
    private int mPlatformMicState = MIC_INITIAL;

    private VoiceResMng(Context context){
        mContext=context;
        initFrontVoiceSetting();
    }

    public static VoiceResMng getmInstance(Context context){
        if(mInstance == null){
            synchronized (VoiceResMng.class){
                mInstance=new VoiceResMng(context);
            }
        }
        return mInstance;
    }

    private void initFrontVoiceSetting(){
        mWorker = new HandlerThread("DeviceNodeThread");
        mWorker.start();
        mControler = DeviceNodeControler.getmInstance();
        mControler.init_config_device();
        mNodeStack= new Stack<>();
        mNodeHandler= new Handler(mWorker.getLooper()){
            public void handleMessage(Message msg){
                switch(msg.what){
                    case REQUEST_MODE:{

                        break;
                    }
                    case RELEASE_MODE:{

                        break;
                    }
                    case UPDATE_WAKEUP_WORK:{
                        break;
                    }
                    default:
                        break;
                }
            }
        };

        mMicPriorityFlag=updateMarkFlag(mMicPriorityFlag, VoiceSvrManager.MIC_REQUEST_TYPE_RECORD,false);
        mMicPriorityFlag=updateMarkFlag(mMicPriorityFlag,VoiceSvrManager.MIC_REQUEST_TYPE_CALL,false);
        mMicPriorityFlag=updateMarkFlag(mMicPriorityFlag,VoiceSvrManager.MIC_REQUEST_TYPE_SYSTEM,false);
        int state= GetValueFromAndValue(mMicPriorityFlag,mPriorityValue,MIC_RELEASE);
        doChangeMicState(state);
    }


    /*
    * 修改控制位标记
    * @param ctlFlag 需要标记的控制量
    * @param priority 控制位
    * public static final int MIC_REQUEST_TYPE_RECORD = 0;
    * public static final int MIC_REQUEST_TYPE_CALL = 1;
    * public static final int MIC_REQUEST_TYPE_SYSTEM = 2;
     */
    private int updateMarkFlag(int ctlFlag, int priority, boolean enable){
        int markFlag = ctlFlag;
        if(enable){
            markFlag=markFlag|(1<<priority);
        } else {
            markFlag=markFlag&(~(1<<priority));
        }
        return  markFlag;
    }

    /*
    * 检验system,Call， Record 是否处于激活状态，
    * 激活状态则返回对应状态值
     */
    private int GetValueFromAndValue(int flag,int[] valueArray, int defaultValue){
        int ret=defaultValue;
        if(checkPriorityisCtl(flag,VoiceSvrManager.MIC_REQUEST_TYPE_SYSTEM)){
            ret = valueArray[VoiceSvrManager.MIC_REQUEST_TYPE_SYSTEM];
            if(ret != defaultValue){
                return  ret;
            }
        }
        if(checkPriorityisCtl(flag, VoiceSvrManager.MIC_REQUEST_TYPE_CALL)){
            ret=valueArray[VoiceSvrManager.MIC_REQUEST_TYPE_CALL];
            if(ret != defaultValue){
                return ret;
            }
        }
        if(checkPriorityisCtl(flag,VoiceSvrManager.MIC_REQUEST_TYPE_RECORD)){
            ret=valueArray[VoiceSvrManager.MIC_REQUEST_TYPE_RECORD];
        }
        return ret;
    }

    /*
    *检查对应的标志为是否为true
     */
    private boolean checkPriorityisCtl(int ctlFlag, int priority){
        return ((ctlFlag & (1<<priority))>0);
    }

    private void doChangeMicState(int state){
        if(mPlatformMicState != state){
            mPlatformMicState = state;
        }
        try {
            if(MIC_HOLD == mPlatformMicState) {
                PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_VIEWOFF);
                PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHOFF);
                prvChangeMode(FrontVoiceWorkMode.WORK_MODE_ECHO_CANCEL);
            } else {
                PlatformService.platformCallback.systemStateChange(PlatformCode.STATE_SPEECHON);
                prvChangeMode(FrontVoiceWorkMode.WORK_MODE_VOICE_WAKEUP);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int prvChangeMode(int mode){
        int ret= mControler.request_work_mode(mode);
        return  ret;
    }

    public boolean requestMic(int requestType,String callingPackageName,IBinder cb){
        return changeMicState(requestType,true,callingPackageName);

    }

    public boolean releaseMic(int requestType, String callingPackageName,IBinder cb){
       return  changeMicState(requestType,false,callingPackageName);
    }


    private boolean changeMicState(int requestType, boolean needState, String callingPackageName){
        switch(requestType){
            case VoiceSvrManager.MIC_REQUEST_TYPE_SYSTEM:{
                break;
            }
            case VoiceSvrManager.MIC_REQUEST_TYPE_CALL:{
                if(needState && (mPlatformMicState==MIC_HOLD)){
                    if(checkPriorityisCtl(mMicPriorityFlag,VoiceSvrManager.MIC_REQUEST_TYPE_CALL)
                            || checkPriorityisCtl(mMicPriorityFlag,VoiceSvrManager.MIC_REQUEST_TYPE_SYSTEM)){
                        return false;
                    }
                }
                break;
            }
            case VoiceSvrManager.MIC_REQUEST_TYPE_RECORD:{
                if(needState && (mPlatformMicState == MIC_HOLD)){
                    return false;
                }
                break;
            }
        }
        mMicPriorityFlag=updateMarkFlag(mMicPriorityFlag,requestType,needState);
        mPriorityValue[requestType]=needState? MIC_HOLD:MIC_RELEASE;
        int state=GetValueFromAndValue(mMicPriorityFlag,mPriorityValue,MIC_RELEASE);
        doChangeMicState(state);
        return true;
    }
}
