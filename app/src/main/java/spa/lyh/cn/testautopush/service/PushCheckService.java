package spa.lyh.cn.testautopush.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


import spa.lyh.cn.testautopush.application.MyApplication;
import spa.lyh.cn.testautopush.push.pushutils.CheckPhoneType;


/**
 * 检查推送是否有活性,这个service不一定需要可以换成别的service
 * Created by liyuhao on 2017/7/18.
 */

public class PushCheckService extends Service {
    private boolean flag;

    private MyApplication app;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //"启动服务,每5分钟检测一次推送服务"
        flag = true;
        Thread t = new Thread(){
            @Override
            public void run() {
                while (flag){
                    try {
                       //Thread.sleep(300000);
                       Thread.sleep(30000);
                        app = (MyApplication) getApplication();
                       if (CheckPhoneType.isEmui()){
                           if (!app.regPush.client.isConnected()){
                               app.regPush.client.connect();
                           }
                       }else {
                        //小米没有检查push活性的api，这个方法暂时不启动
                       }
                    }catch (Exception e){
                        flag = false;
                    }
                }
            }
        };
        //t.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}
