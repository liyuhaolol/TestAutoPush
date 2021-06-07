package spa.lyh.cn.testautopush.push;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.lang.reflect.Method;
import java.util.List;

import spa.lyh.cn.testautopush.push.pushutils.CheckPhoneType;


/**
 * 推送消息的统一入口
 * Created by liyuhao on 2017/8/8.
 */

public class RegPushService{

    private static final String TAG = "RegPushService";
    //小米推送的APP_ID
    public static final String APP_ID = "2882303761517603314";
    //小米推送的APP_KEY
    public static final String APP_KEY = "5641760366314";


    public void init(Context context){

        MiPushClient.registerPush(context, APP_ID, APP_KEY);
        Log.e(TAG,"start xiaomi Push");
        //打开Log
        initMiPushLogger(context);
    }

    private void initMiPushLogger(Context context) {
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(context, newLogger);
    }
}
