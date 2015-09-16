package mo.com.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * 作者：MoMxMo on 2015/9/16 09:09
 * 邮箱：xxxx@qq.com
 */


public class TrafficBean {
    public Drawable icon;
    public String app_name;
    public long inTraffic;    //接收 rcv
    public long outTraffic;   //发送  snd

    @Override
    public String toString() {
        return "TrafficBean{" +
                "icon=" + icon +
                ", app_name='" + app_name + '\'' +
                ", inTraffic=" + inTraffic +
                ", outTraffic=" + outTraffic +
                '}';
    }
}
