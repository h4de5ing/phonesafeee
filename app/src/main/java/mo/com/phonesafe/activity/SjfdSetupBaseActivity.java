package mo.com.phonesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import mo.com.phonesafe.R;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public abstract class SjfdSetupBaseActivity extends Activity {
    private static final String TAG = "SjfdSetup1Activity";

    private GestureDetector mGestrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGestrue = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            //监听手势识别器的动作
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //  获取X轴的开始位置
                float x1 = e1.getX();
                //获取X轴的最后位置
                float x2 = e2.getX();

                float y1 = e1.getY();   //Y开始位置
                float y2 = e2.getY();   //Y结束位置


                if(Math.abs(y2-y1) > Math.abs(x2-x1)) {
                    //如果Y的滑动距离大于X则不进行任何操作
                    Log.i(TAG,"Y滑动距离大于X");
                    return false;
                }

                if (Math.abs(velocityX) < 100) {
                    /*如果x方向的速率小于100则不进行任何的操作*/
                    Log.i(TAG,"速率过慢");
                    return false;
                }

                if (x1 > x2) {
                    /*向下一步滑动*/
                    preformNext();
                    return true;
                }else{
                    //向下一步滑动
                    preformPre();
                    return true;
                }

                //不继续分发消费
//                return false;

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
        //手势识别分析器
        mGestrue.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 手指滑动的事件监听
     */


    //执行上一步操作
    private void preformPre() {
        if (doPre()) {
            //用户有下一步的操作
            return;
        }
        // 2.共同点
        // 动画
        // enterAnim: 进入的动画, 让进入的activity做动画
        // exitAnim: 退出的动画,让退出的activity做动画
//        overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);  水平位移
        overridePendingTransition(R.anim.pre_enter_rotate, R.anim.pre_exit_rotate);

        finish();
    }

    /**
     * 执行下一步操作
     */
    private void preformNext() {
        if (doNext()) {
            //执行下一步的操作
            return;
        }

        // 2.共同点
        // 动画
        // enterAnim: 进入的动画, 让进入的activity做动画
        // exitAnim: 退出的动画,让退出的activity做动画
        overridePendingTransition(R.anim.next_enter_rotate, R.anim.next_exit_rotate);
        finish();
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
