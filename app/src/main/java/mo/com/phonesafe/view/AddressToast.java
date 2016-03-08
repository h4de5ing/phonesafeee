package mo.com.phonesafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * Created by Gh0st on 2015/9/6 19:28
 * <p/>
 * 显示号码归属地的自定义Toast
 */

public class AddressToast implements View.OnTouchListener {
    private View mView;
    private WindowManager mWM;
    private WindowManager.LayoutParams mParams;
    private float mDownX;
    private float mDownY;
    private final LinearLayout ll_style;
    private Context mContext;
    public AddressToast(Context context) {
        mContext = context;
        //1.WindowManager
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //2.显示View
        mView = View.inflate(context, R.layout.addresstoast, null);
        ll_style = (LinearLayout) mView.findViewById(R.id.ll_address_style);

        //3.WindowManager.LayoutParams
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

        mView.setOnTouchListener(this);
    }

    //显示自定义的Toast样式
    public void show(String text) {
        hide();
        TextView tv = (TextView) mView.findViewById(R.id.tv_address_show);
        tv.setText(text);

        /*当用户还没有选择的时候，默认显示样式*/
        if (PreferenceUtils.getInt(mContext, Constants.ADDRESS_STYLE) == -1) {
            ll_style.setBackgroundResource( R.drawable.address_shape_color_yellow);
        } else {
            ll_style.setBackgroundResource(PreferenceUtils.getInt(mContext, Constants.ADDRESS_STYLE));
        }
        //显示
        mWM.addView(mView, mParams);
    }

    //隐藏自定义Toast
    public void hide() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
        }
    }

    /**
     * 触摸移动的焦点事件（自定义的Toast是可以进行移动的）
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveX = event.getRawX();
                float mMoveY = event.getRawY();

                float diffX = mMoveX - mDownX;
                float diffY = mMoveY - mDownY;

                mParams.x += diffX;
                mParams.y += diffY;

                mWM.updateViewLayout(mView,mParams);

                mDownX = mMoveX;
                mDownY = mMoveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
}
