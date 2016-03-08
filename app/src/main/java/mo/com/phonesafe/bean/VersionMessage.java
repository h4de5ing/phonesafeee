package mo.com.phonesafe.bean;

/**
 * Created by Gh0st on 2015/8/28 20:14
 *
 * 更新版本的信息
 */


public class VersionMessage {

    private int versionCode;
    private String desc;
    private String downloadurl;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    @Override
    public String toString() {
        return "VersionMessage{" +
                "versionCode=" + versionCode +
                ", desc='" + desc + '\'' +
                ", downloadurl='" + downloadurl + '\'' +
                '}';
    }
}
