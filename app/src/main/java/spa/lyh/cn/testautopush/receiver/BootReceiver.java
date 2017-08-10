package spa.lyh.cn.testautopush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import spa.lyh.cn.testautopush.service.PushCheckService;

/**
 * Created by liyuhao on 2017/7/18.
 */

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            //启动任意的service都会启动application并启动推送
            Intent newIntent = new Intent(context,PushCheckService.class);
            context.startService(newIntent);
        }
    }
}