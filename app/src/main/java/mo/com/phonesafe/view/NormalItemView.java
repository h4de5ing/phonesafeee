package mo.com.phonesafe.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mo.com.phonesafe.R;

/**
 * 作者：MoMxMo on 2015/9/11 20:03
 * 邮箱：xxxx@qq.com
 */


public class NormalItemView extends RelativeLayout {
    private static final int BACKGROUND_BLUE = 0;
    private static final int BACKGROUND_GREEN = 1;
    private static final int BACKGROUND_PURPLE = 2;
    private static final int TITILE_COLOR = 3;


    private ImageView mIvIcon;  //图标
    private ImageView mIvGo;    //下个Acitivity图标
    private TextView mTvTile;  //设置的文本
    private RelativeLayout rl_item_normal;

    //java
    public NormalItemView(Context context) {
        super(context);
    }

    //xml
    public NormalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /*使用打气筒获取布局文件*/
        View.inflate(context, R.layout.view_item_normal, this);
        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
        mTvTile = (TextView) findViewById(R.id.tv_title);
        mIvGo = (ImageView) findViewById(R.id.iv_go);
        rl_item_normal = (RelativeLayout) findViewById(R.id.rl_item_normal);

        //读属性值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NormalItemView);


        //读取属性
        String mTitle = ta.getString(R.styleable.NormalItemView_nivTile);

        //文本颜色
        ColorStateList mTitleColor = ta.getColorStateList(R.styleable.NormalItemView_nivTextColor);

        //背景颜色
        int background = ta.getInt(R.styleable.NormalItemView_nivBackground, BACKGROUND_BLUE);

        //是否有go
        boolean goFlag = ta.getBoolean(R.styleable.NormalItemView_nivGo, true);

        //图标
        Drawable d = ta.getDrawable(R.styleable.NormalItemView_nivIcon);
        ta.recycle();

        /*设置Title文本颜色属性*/
        if (mTitleColor!=null) {
            mTvTile.setTextColor(mTitleColor);
        }

        /*设置Title文本颜色*/
        if (mTitle!=null) {
            mTvTile.setText(mTitle);
        }

        /*设置背景*/
        switch (background) {
            case BACKGROUND_BLUE:
                rl_item_normal.setBackgroundResource(R.drawable.item_setting_blue_selector);
                break;
            case BACKGROUND_GREEN:
                rl_item_normal.setBackgroundResource(R.drawable.item_setting_green_selector);
                break;
            case BACKGROUND_PURPLE:
                rl_item_normal.setBackgroundResource(R.drawable.item_setting_purple_selector);
                break;
            default:
                break;
        }
        //设置默认是否有下一跳
        mIvGo.setVisibility(goFlag ? View.VISIBLE : View.GONE);

        if (d != null) {
        mIvIcon.setImageDrawable(d);
        }

    }

    /**
     * 设置标题文本
     *
     */
    public void setmTvTile(String title){
        mTvTile.setText(title);
    }

    /**
     * 设置文本的颜色
     */
    public void setmTvTileColor(int color) {
        mTvTile.setTextColor(color);
    }

    /**
     * 设置图标资源
     */
    public void setmIvIcon(Drawable drawable) {
        mIvIcon.setImageDrawable(drawable);
    }


}
