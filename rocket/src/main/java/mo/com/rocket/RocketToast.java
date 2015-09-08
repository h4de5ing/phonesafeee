package mo.com.rocket;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 小火箭
 *
 * 作者：MoMxMo on 2015/9/6 22:11
 * 邮箱：xxxx@qq.com
 */


public class RocketToast   implements View.OnTouchListener{
    private static final String TAG = "RocketToast";
    private WindowManager mWM;
    private WindowManager.LayoutParams mParams_rocket;
    private WindowManager.LayoutParams mParams_tip;
    private ImageView mView_rocket;
    private ImageView mView_tip;
    private float mDownX;
    private float mDownY;
    private final AnimationDrawable rocketAnimation;
    private final AnimationDrawable tipAnimation;
    private Context mContext;
    private boolean inZone = false;

    public RocketToast(Context context) {

        mContext = context;
        //1.WindowManager
        mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        //2.显示View

        //火箭动画-------------------------------------------------------

        //a)定义火箭动画
        mView_rocket = new ImageView(context);
        mView_rocket.setBackgroundResource(R.drawable.rocket);
        rocketAnimation = (AnimationDrawable) mView_rocket.getBackground();

        //b)设置火箭动画属性
        mParams_rocket = new WindowManager.LayoutParams();
        mParams_rocket.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams_rocket.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams_rocket.format = PixelFormat.TRANSLUCENT;
        mParams_rocket.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams_rocket.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mParams_rocket.gravity = Gravity.LEFT | Gravity.TOP; //显示在左上角

        //c)火箭动画触摸事件监听
        mView_rocket.setOnTouchListener(this);


        //提示动画------------------------------------------------------

        //定义提示动画
        mView_tip = new ImageView(context);
        mView_tip.setBackgroundResource(R.drawable.tip);
        tipAnimation = (AnimationDrawable) mView_tip.getBackground();

        //设置提示动画属性
        mParams_tip = new WindowManager.LayoutParams();
        mParams_tip.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams_tip.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams_tip.format = PixelFormat.TRANSLUCENT;
        mParams_tip.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams_tip.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mParams_tip.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        //发射动画


    }

    public void show() {
        //显示火箭
         hide();
        rocketAnimation.start();
        mWM.addView(mView_rocket, mParams_rocket);
    }
    public void showTip() {
        //显示提示
         hideTip();
        tipAnimation.start();
        mWM.addView(mView_tip, mParams_tip);
    }

    public void hideTip() {
        //隐藏提示
        if (mView_tip != null) {
            if (mView_tip.getParent() != null) {
                mView_tip.setImageDrawable(null);
                tipAnimation.stop();
                mWM.removeView(mView_tip);
            }
        }
    }
    public void hide() {
        //隐藏火箭
        if (mView_rocket != null) {
            if (mView_rocket.getParent() != null) {
                mView_rocket.setImageDrawable(null);
                rocketAnimation.stop();
                mWM.removeView(mView_rocket);
            }
        }
    }
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();

                //显示提示
                showTip();

                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveX = event.getRawX();
                float mMoveY = event.getRawY();

                float diffX = mMoveX - mDownX;
                float diffY = mMoveY - mDownY;

                mParams_rocket.x += diffX;
                mParams_rocket.y += diffY;

                mWM.updateViewLayout(mView_rocket, mParams_rocket);

                //判断小火箭是否已经进入提示的区域
                if (enterTip() && !inZone) {
                    Log.d(TAG, "onTouch 进入区域");
                    tipAnimation.stop();
                    mView_tip.setImageDrawable(null);
                    mView_tip.setBackgroundResource(R.drawable.desktop_bg_tips_3);
                    inZone = true;
                }else if (!enterTip() && inZone) {
                    Log.d(TAG, "onTouch 不在区域");
                    mView_tip.setImageDrawable(tipAnimation);
                    tipAnimation.start();
                    //mWM.updateViewLayout(mView_rocket,mParams_rocket);
                    inZone = false;
                }

                mDownX = mMoveX;
                mDownY = mMoveY;
                break;
            case MotionEvent.ACTION_UP:


                if (enterTip()) {
                    //小火箭到中间
                    DisplayMetrics metrics = mContext.getResources()
                            .getDisplayMetrics();

                    //获取屏幕中间的位置
                    int widthPixels = metrics.widthPixels;
                    int heightPixels = metrics.heightPixels;
                    mParams_rocket.x = ((int) (widthPixels/2f - mView_rocket.getWidth()/2f));
                    mParams_rocket.y = heightPixels - mView_rocket.getHeight();
                    mWM.updateViewLayout(mView_rocket,mParams_rocket);

                    //发射动画
                    int start = mParams_rocket.y;
                    ValueAnimator animator = ValueAnimator.ofInt(start, 0);
                    animator.setDuration(600);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            //实时更新动画的显示
                            int values = (Integer) animation.getAnimatedValue();

                            mParams_rocket.y = values;

                            mWM.updateViewLayout(mView_rocket,mParams_rocket);

                        }
                    });

                    animator.start();

                    //开启烟雾效果
                    Intent intent = new Intent(mContext, SmokeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }

                //隐藏提示
                hideTip();

                break;
        }
        return true;
    }

    /**
     * 判断小火箭是否已经进入到提示区域
     * @return
     */
    private boolean enterTip() {
        //1.获取小火箭的位置坐标
        int[] rocket_location = new int[2];
        mView_rocket.getLocationOnScreen(rocket_location);
        int rocketX =   rocket_location[0];    //小火箭的X轴位置
        int rocketY =   rocket_location[1];    //小火箭的X轴位置

//        Log.i(TAG, "小火箭的位置： "+rocketX +":"+ rocketY);

        //2.获取提示的位置坐标
        int[] tip_location = new int[2];
        mView_tip.getLocationOnScreen(tip_location);
        int tipX =   tip_location[0];    //小火箭的X轴位置
        int tipY =   tip_location[1];    //小火箭的X轴位置

        //判断X轴方向，小火箭的X轴+宽度的一半  要大于提示的X轴位置
        boolean x = (rocketX+mView_rocket.getWidth()/2f) > tipX && (rocketX+mView_rocket.getWidth()/2f) < tipX+mView_tip.getWidth();

        //判断Y轴方向，小火箭的Y轴+高度的一半 要大于提示的Y轴的位置
        boolean y = (rocketY + mView_rocket.getHeight() / 2f) > tipY;

        if (x && y) {
            return true;
        }
        return false;
    }

}
