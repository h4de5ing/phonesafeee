package mo.com.phonesafe.bean;

/**
 * 作者：MoMxMo on 2015/9/11 22:11
 * 邮箱：xxxx@qq.com
 */


public class SmsBean {
    public String _id;
    public String address;
    public String date;
    public String body;
    public int type;

    @Override
    public String toString() {
        return "SmsBean{" +
                "_id='" + _id + '\'' +
                ", address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", body='" + body + '\'' +
                ", type=" + type +
                '}';
    }
}
