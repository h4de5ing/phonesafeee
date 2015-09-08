package mo.com.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * APP管理Bean
 *
 * 作者：MoMxMo on 2015/9/8 13:11
 * 邮箱：xxxx@qq.com
 */


public class AppBean {
    public Drawable icon;   //应用的图标
    public String name;     //应用的名称
    public String packageName;  //应用的包名
    public long size;       //应用的大小
    public boolean isInstallSD; //安装在手机SD卡
    public boolean isSystem; //是否是系统应用


}
