package spa.lyh.cn.testautopush.push.model.base;

/**
 * 穿透消息的基础类
 * Created by liyuhao on 2017/8/9.
 */

public class PushThrowMessage<T> {
    //消息类型
    public int type;
    //额外的参数
    public T extras;
}
