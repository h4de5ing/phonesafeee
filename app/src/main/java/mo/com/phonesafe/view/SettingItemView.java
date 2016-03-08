package mo.com.phonesafe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mo.com.phonesafe.R;

/**
 * Created by Gh0st on 2015/8/29 22:05
 *
 * 自定义setting控件
 */


public class SettingItemView extends RelativeLayout {
    private static final int BACKGROUND_BLUE = 0;
    private static final int BACKGROUND_GREEN = 1;
    private static final int BACKGROUND_PURPLE = 2;
    TextView mTvTitle;
    ImageView mIvIcon;
    private boolean isOpen = true;  //默认是关闭的

    /**
     * java的实例对象使用
     *
     * @param context
     */
    public SettingItemView(Context context) {
        super(context, null);
    }

    /**
     * xml使用
     *
     * @param context
     * @param set
     */
    public SettingItemView(Context context, AttributeSet set) {
        super(context, set);

        View.inflate(context, R.layout.view_setting_item, this);

        mTvTitle = (TextView) findViewById(R.id.view_tv_title);
        mIvIcon = (ImageView) findViewById(R.id.view_iv_icon);
        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.SettingItemView);
        String title = ta.getString(R.styleable.SettingItemView_sivTitle);
        int background = ta.getInt(R.styleable.SettingItemView_sivBackground,BACKGROUND_BLUE);
        boolean flag = ta.getBoolean(R.styleable.SettingItemView_sivToggle,true);
        ta.recycle();
        mTvTitle.setText(title);
        switch (background){
            case BACKGROUND_BLUE:
                findViewById(R.id.view_root).setBackgroundResource(R.drawable.item_setting_blue_selector);
                break;
            case BACKGROUND_GREEN:
                findViewById(R.id.view_root).setBackgroundResource(R.drawable.item_setting_green_selector);
                break;
            case BACKGROUND_PURPLE:
                findViewById(R.id.view_root).setBackgroundResource(R.drawable.item_setting_purple_selector);
                break;
            default:
                break;
        }

        //设置开关
        mIvIcon.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    /**
     * 开关的方法
     *
     */
    public void toggle(){
        //开的时候关：关的时候关
        mIvIcon.setImageResource(isOpen ? R.mipmap.zg : R.mipmap.vg);
        isOpen = !isOpen;
    }

    /**
     * 获取开关状态
     * @return
     */
    public boolean getToggleState(){
        return isOpen;
    }
    /**
     * 设置开关状态
     */
    public void setToggleState(boolean flag) {
        isOpen = flag;
        mIvIcon.setImageResource(isOpen?R.mipmap.vg:R.mipmap.zg);
    }

}
