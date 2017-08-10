package spa.lyh.cn.testautopush.push.receiver.huawei;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

import spa.lyh.cn.testautopush.push.PushMessageHandler;
import spa.lyh.cn.testautopush.push.RegPushService;

/**
 * 华为的receiver
 * Created by liyuhao on 2017/8/8.
 */

public class HuaweiMessageReceiver extends PushReceiver{

    public static final String TAG = "HuaweiMessageRevicer";

    public static final String ACTION_UPDATEUI = "action.updateUI";

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        Log.e(TAG, "belongId为:" + belongId);
        Log.e(TAG, "Token为:" + token);
        //把token赋值，也可以在这里上传服务器
        RegPushService.HuaweiToken = token;
        //保存在本地备用
        SharedPreferences sp = context.getSharedPreferences("huawei_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token",token);
        editor.apply();


        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATEUI);
        intent.putExtra("type", 1);
        intent.putExtra("token", token);
        context.sendBroadcast(intent);
    }

    /**
     * 这里接收到穿透消息，自定义通知栏，和操作
     * @param context 上下文
     * @param msg 消息
     * @param bundle bundle
     * @return 不清楚返回真假的作用
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            //CP可以自己解析消息内容，然后做相应的处理
            String content = new String(msg, "UTF-8");
            Log.e(TAG, "收到PUSH透传消息,消息内容为:" + content);
            //交付统一消息处理出口
            PushMessageHandler.syncMessage(context,content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 这里接收到的是通知栏消息，无法自定义操作，默认打开应用
     * @param context 上下文
     * @param event 事件
     * @param extras 额外
     */
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            //Log.e(TAG, "收到通知栏消息点击事件,notifyId:" + notifyId);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
        }

        String message = extras.getString(BOUND_KEY.pushMsgKey);
        super.onEvent(context, event, extras);
    }

    /**
     * 得到华为推送的连接状态
     * @param context 上下文
     * @param pushState 连接状态
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        //Log.e("TAG", "Push连接状态为:" + pushState);

        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATEUI);
        intent.putExtra("type", 2);
        intent.putExtra("pushState", pushState);
        context.sendBroadcast(intent);
    }
}
