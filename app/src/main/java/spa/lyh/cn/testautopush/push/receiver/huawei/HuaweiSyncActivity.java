package spa.lyh.cn.testautopush.push.receiver.huawei;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import spa.lyh.cn.testautopush.push.PushMessageHandler;

/**
 * 华为的分析activity
 * Created by liyuhao on 2017/8/9.
 */

public class HuaweiSyncActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Uri uri = i.getData();
        String json;
        if (uri != null){
            // 获取uri中携带的参数，多个参数都可以这样获取
            json = uri.getQueryParameter("json");
            if (json != null){
                //交付统一消息处理出口
                PushMessageHandler.syncMessage(getApplicationContext(),json);
                finish();
            }else {
                finish();
            }
        }
    }
}
