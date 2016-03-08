package mo.com.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Gh0st on 2015/9/16 09:09
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
