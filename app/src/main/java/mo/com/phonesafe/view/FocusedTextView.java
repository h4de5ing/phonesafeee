package mo.com.phonesafe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Gh0st on 2015/8/29 19:55
 *
 * 自定义----------走马灯字体
 *
 */


public class FocusedTextView extends TextView {

    /**
     * java使用这个构造方法实例化对象
     * @param context
     */
    public FocusedTextView(Context context) {
        super(context,null);
    }

    /**
     * xml文件使用这个构造方法
     * @param context
     * @param attrs
     */
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //设置默认的一些属性
        setSingleLine();
        //设置走马灯效果
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //循环播放
        setMarqueeRepeatLimit(-1);
        //设置焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    //欺骗当前的Acitivity（Window）可以获的焦点
    @Override
    public boolean isFocused() {
        /*return super.isFocused();*/
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused){
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(hasWindowFocus){
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }

}
