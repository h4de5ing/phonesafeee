package mo.com.phonesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import mo.com.phonesafe.R;

/**
 * 作者：MoMxMo on 2015/8/29 22:05
 * 邮箱：xxxx@qq.com
 */


public class SettingItemView extends RelativeLayout {

    /**
     * java的实例对象使用
     * @param context
     */
    public SettingItemView(Context context) {
        super(context,null);
    }

    /**
     * xml使用
     * @param context
     * @param attrs
     */
    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //挂载xml布局，xml和当前类绑定
        View.inflate(context, R.layout.view_setting_item,this);
    }
}
