package mo.com.phonesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import mo.com.phonesafe.R;

/**
 * Created by Gh0st on 2015/9/12 19:43
 * <p/>
 * 选择
 */


public class SegementView extends LinearLayout {

    TextView tv_lock;
    TextView tv_unlock;
    OnSegementSelectListener mListener;
    boolean isLocked=true;

    public SegementView(Context context) {
        super(context);
    }

    public SegementView(Context context, AttributeSet attrs) {

        super(context, attrs);
        View.inflate(context, R.layout.view_segement, this);

        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        initEvent();

        tv_lock.setSelected(true);
        tv_unlock.setSelected(false);
    }

    private void initEvent() {
        tv_lock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isLocked) {
                    if (mListener != null) {
                        //上锁
                        tv_lock.setSelected(true);
                        tv_unlock.setSelected(false);

                        mListener.onSelected(true);

                        isLocked = true;
                    }
                }


            }
        });

        tv_unlock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocked) {
                    if (mListener != null) {
                        tv_lock.setSelected(false);
                        tv_unlock.setSelected(true);
                        mListener.onSelected(false);
                        isLocked = false;
                    }
                }


            }
        });
    }

    //暴露监听的接口
    public void setOnSegementSelectListener(OnSegementSelectListener linstener) {
        this.mListener = linstener;
    };

    //监听器
    public interface OnSegementSelectListener {

        /**
         * 选择的回调，如果为true上锁
         * @param locked
         */
        void onSelected(boolean locked);
    }
}
