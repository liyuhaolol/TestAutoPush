package spa.lyh.cn.testautopush.push;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import spa.lyh.cn.testautopush.etc.SilenceUtils;
import spa.lyh.cn.testautopush.push.model.NotificationSample;
import spa.lyh.cn.testautopush.push.model.SilenceSample;
import spa.lyh.cn.testautopush.push.model.base.PushThrowMessage;


/**
 * 推送消息处理的统一出口
 * Created by liyuhao on 2017/8/9.
 */

public class PushMessageHandler {
    private static final String TAG = "PushMessageHandler";

    private static final int NOTIFICATION_MSG = 0;

    private static final int SILENCE_MSG = 1;

    /**
     * 初步解析得到消息
     * @param context 上下文
     * @param msg json
     */
    public static void syncMessage(Context context,String msg){
        try {
            PushThrowMessage pMsg = JSONObject.parseObject(msg,PushThrowMessage.class);
            switch (pMsg.type){
                case NOTIFICATION_MSG:
                    notification(context,msg);
                    break;
                case SILENCE_MSG:
                    silence(context,msg);
                    break;
            }

        }catch (Exception e){
            Log.e(TAG,"wrong json");
        }
    }

    /**
     * 执行静默推送逻辑,这里就Toast一句话做个例子
     * @param context 上下文
     * @param msg json
     */
    private static void silence(Context context, String msg){
        try {
            PushThrowMessage<SilenceSample> pMsg = JSONObject
                    .parseObject(msg,new TypeReference<PushThrowMessage<SilenceSample>>(){});
            if (pMsg.extras != null){
                //执行对应的静默推送逻辑
                switch (pMsg.extras.silenceType){
                    case SilenceUtils.TEXT:
                        SilenceUtils.setText();
                        break;
                    case SilenceUtils.TOAST:
                        SilenceUtils.ToastMsg();
                        break;
                }
            }

        }catch (Exception e){
            Log.e(TAG,"wrong json");
        }
    }

    /**
     * 显示对应通知
     * @param context 上下文
     * @param msg json
     */
    private static void notification(Context context,String msg){
        try {
            PushThrowMessage<NotificationSample> pMsg = JSONObject
                    .parseObject(msg,new TypeReference<PushThrowMessage<NotificationSample>>(){});
            if (pMsg.extras != null){
                try {
                    Class clazz = Class.forName(pMsg.extras.target);
                    Intent i = new Intent(context,clazz);
                    i.putExtra("msg",pMsg.extras.message);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            Log.e(TAG,"wrong json");
        }
    }
}
