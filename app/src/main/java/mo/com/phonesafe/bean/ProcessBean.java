package mo.com.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Gh0st on 2015/9/9 22:46
 *
 *
 * 应用进程Bean
 */


public class ProcessBean {

    public Drawable icon;
    public String name;
    public long memory;
    public boolean isSystem;    //是否是系统进程
    public String packageName;
    public boolean isSelected;// 判断是否选中

}
