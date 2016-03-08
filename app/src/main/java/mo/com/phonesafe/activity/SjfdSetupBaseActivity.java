package mo.com.phonesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import mo.com.phonesafe.R;

/**
 * Created by Gh0st on 2015/8/30 20:47
 */


public abstract class SjfdSetupBaseActivity extends Activity {

    private GestureDetector mGestrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGestrue = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float x1 = e1.getX();
                float x2 = e2.getX();

                float y1 = e1.getY();   //Y开始位置
                float y2 = e2.getY();   //Y结束位置

                if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
                    //如果Y的滑动距离大于X则不进行任何操作
                    return false;
                }

                if (Math.abs(velocityX) < 100) {
                    /*如果x方向的速率小于100则不进行任何的操作*/
                    return false;
                }

                if (x1 > x2) { /*向下一步滑动*/
                    preformNext();
                    return true;
                } else {  //向下一步滑动
                    preformPre();
                    return true;
                }
            }
        });
    }


    /**
     * 关联手势识别器
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestrue.onTouchEvent(event); //手势识别分析器
        return super.onTouchEvent(event);
    }

    private void preformPre() {
        if (doPre()) {
            return;
        }
        finish();
        overridePendingTransition(R.anim.pre_enter_rotate, R.anim.pre_exit_rotate);
    }

    private void preformNext() {
        if (doNext()) {
            return;
        }
        finish();
        overridePendingTransition(R.anim.next_enter_rotate, R.anim.next_exit_rotate);
    }

    /**
     * 上一步的点击事件
     */
    public void ClickPre(View view) {
        preformPre();
    }

    /**
     * 下一步的点击事件（共同的操作）
     */
    public void ClickNext(View view) {
        preformNext();
    }

    /**
     * 执行上一个页面操作，如果return true不往下执行
     *
     * @return
     */
    protected abstract boolean doPre();

    /**
     * 执行下一个页面操作，如果return true不往下执行
     *
     * @return
     */
    protected abstract boolean doNext();
}
