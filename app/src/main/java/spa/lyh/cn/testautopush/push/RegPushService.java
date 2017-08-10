package spa.lyh.cn.testautopush.push;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;

import com.huawei.hms.api.HuaweiApiClient.ConnectionCallbacks;
import com.huawei.hms.api.HuaweiApiClient.OnConnectionFailedListener;
import com.huawei.hms.support.api.push.PushException;
import com.huawei.hms.support.api.push.TokenResult;
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

public class RegPushService implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final String TAG = "RegPushService";
    //小米推送的APP_ID
    public static final String APP_ID = "2882303761517603314";
    //小米推送的APP_KEY
    public static final String APP_KEY = "5641760366314";

    public static String HuaweiToken;

    //华为移动服务Client
    public HuaweiApiClient client;


    public void init(Context context){

        if (CheckPhoneType.isEmui()){
            initHuaweiPush(context);
            Log.e(TAG,"start huawei Push");
        }else {
            if(shouldInit(context)) {
                MiPushClient.registerPush(context, APP_ID, APP_KEY);
                Log.e(TAG,"start xiaomi Push");
            }
            //打开Log
            initMiPushLogger(context);
        }



    }


    private void initHuaweiPush(Context context){
        //this.app = app;

        //创建华为移动服务client实例用以使用华为push服务
        //需要指定api为HuaweiId.PUSH_API
        //连接回调以及连接失败监听
        client = new HuaweiApiClient.Builder(context)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //建议在oncreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        client.connect();
    }

    /**
     * 断开华为PUSH的连接
     */
    public void disconnectPush(){
        if (client != null && client.isConnected()){
            client.disconnect();
        }
    }

    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        //Log.e("TestAutoPush", "HuaweiApiClient 连接成功");
        //getTokenAsyn();
        getTokenSync();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //HuaweiApiClient断开连接的时候，业务可以处理自己的事件
        Log.e("TestAutoPush", "HuaweiPush连接断开");
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改
        if (client != null) {
            client.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TestAutoPush", "HuaweiApiClient连接失败，错误码：" + connectionResult.getErrorCode());
    }


    /**
     * 使用同步接口来获取pushtoken
     * 结果通过广播的方式发送给应用，不通过标准接口的pendingResul返回
     * 开发者可以自行处理获取到token
     * 同步获取token和异步获取token的方法开发者只要根据自身需要选取一种方式即可
     */
    private void getTokenSync() {
        if(!client.isConnected()) {
            Log.e("TestAutoPush", "获取token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }

        //需要在子线程中调用函数
        new Thread() {

            public void run() {
                Log.e("TestAutoPush", "同步接口获取push token");
                PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
                TokenResult result = tokenResult.await();
                if(result.getTokenRes().getRetCode() == 0) {
                    //当返回值为0的时候表明获取token结果调用成功
                        Log.e("TestAutoPush", "获取push token 成功 等待广播");

                }
            }
        }.start();
    }



    /**
     * 使用异步接口来获取pushtoken
     * 结果通过广播的方式发送给应用，不通过标准接口的pendingResul返回
     * 开发者可以自行处理获取到token
     * 同步获取token和异步获取token的方法开发者只要根据自身需要选取一种方式即可
     */
    private void getTokenAsyn() {
        if(!client.isConnected()) {
            Log.e("TestAutoPush", "获取token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }

        Log.e("TestAutoPush", "异步接口获取push token");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

            @Override
            public void onResult(TokenResult result) {
                //这边的结果只表明接口调用成功，是否能收到响应结果只在广播中接收
                if (result.getTokenRes().getRetCode() == 0){
                    //当返回值为0的时候表明获取token结果调用成功
                    Log.e("TestAutoPush", "获取push token 成功 等待广播"+result.getTokenRes().getToken());
                }
            }
        });
    }

    /**
     * 应用删除通过getToken接口获取到的token
     * 应用调用注销token接口成功后，客户端就不会再接收到PUSH消息
     * 开发者应该在调用该方法后，自行处理本地保存的通过gettoken接口获取到的TOKEN
     */
    private void deleteToken() {

        if(!client.isConnected()) {
            Log.e("TestAutoPush", "注销token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }

        //需要在子线程中执行删除token操作
        new Thread() {
            @Override
            public void run() {
                //调用删除token需要传入通过getToken接口获取到token，并且需要对token进行非空判断
                String token = HuaweiToken;

                if (!token.equals("")){
                    Log.e("TestAutoPush", "删除Token：" + token);
                    if (!TextUtils.isEmpty(token)){
                        try {
                            HuaweiPush.HuaweiPushApi.deleteToken(client, token);
                        } catch (PushException e) {
                            Log.e("TestAutoPush", "删除Token失败:" + e.getMessage());
                        }
                    }
                }
            }
        }.start();
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

    private boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
