package spa.lyh.cn.testautopush.application;

import android.app.Application;

import spa.lyh.cn.testautopush.push.RegPushService;

/**
 * Created by liyuhao on 2017/8/4.
 */

public class MyApplication extends Application {


    public RegPushService regPush;


    @Override
    public void onCreate() {
        super.onCreate();
        //初始化push推送服务
        regPush = new RegPushService();
        regPush.init(this);
    }


}
