package com.voiceservice.core.frontvoice;

import android.os.IBinder;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/7/20.
 */

public class DeviceNodeControler {
    private static final String TAG= "DeviceNodeControler";
    private static DeviceNodeControler mInstance;
    private static final String DEVICE_NODE_DIR=
            "/sys/bus/i2c/drivers/xf6000ye/1-0047/";
    private static final String WORK_FUNC_NODE="func";
    private static final String OUTPUT_VOLUME_NODE="output_db";
    private static final String WEKEUP_OUTPUT_NODE="status";
    private static final String WEKEUP_WORD_NODE="wakeid";

    private DeviceNodeControler(){

    }

    public static DeviceNodeControler getmInstance(){
        if(mInstance == null){
            mInstance=new DeviceNodeControler();
        }
        return mInstance;
    }

    private String prv_get_node_file_content(String node){
        String ret =null;

        String nodePath = DEVICE_NODE_DIR + node;
        int size=0;
        byte[] buffer = new byte[16];

        try{
            FileInputStream in =new FileInputStream(nodePath);
            size  = in.read(buffer);

            if(size >0){
                byte[] tmp= new byte[size];
                for(int i= 0; i< size; i++){
                    tmp[i]=buffer[i];
                }
                ret= new String(tmp);
            }
            in.close();
        } catch ( Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    private void prv_set_node_file_content(String node, String value){
        if(value == null){
            return;
        }
        String nodePath= DEVICE_NODE_DIR+node;
        try {
            FileOutputStream out = new FileOutputStream(nodePath);
            out.write(value.getBytes());
            out.flush();
            out.close();
        } catch ( Exception e){
            e.printStackTrace();
        }
    }

public int request_work_mode(int mode){
    int ret = -1;
    switch(mode){
        case FrontVoiceWorkMode.WORK_MODE_PASSBY:
            prv_set_node_file_content(WEKEUP_WORD_NODE,"0");
            break;
        case FrontVoiceWorkMode.WORK_MODE_NOISECLEAN:
            prv_set_node_file_content(WEKEUP_WORD_NODE,"1");
            break;
        case FrontVoiceWorkMode.WORK_MODE_ECHO_CANCEL:
            prv_set_node_file_content(WEKEUP_WORD_NODE,"3");
            break;
        default:
            break;
    }
    if(get_current_work_mode() == mode){
        ret=0;
    }
    return ret;
}


    public int get_current_work_mode(){
        int ret = -1;
        String var= null;
        var = prv_get_node_file_content(WEKEUP_WORD_NODE);
        switch (var.charAt(0)){
            case '0':
                ret=FrontVoiceWorkMode.WORK_MODE_PASSBY;
                break;
            case '1':
                ret=FrontVoiceWorkMode.WORK_MODE_NOISECLEAN;
                break;
            case '3':
                ret=FrontVoiceWorkMode.WORK_MODE_ECHO_CANCEL;
                break;
            default:
                break;
        }
        return ret;
    }

    public void init_config_device(){
        prv_set_node_file_content(WEKEUP_WORD_NODE,String.valueOf(FrontVoiceWorkMode.WORK_MODE_DEFAULT));
        prv_set_node_file_content(WORK_FUNC_NODE,"0");
    }

}
