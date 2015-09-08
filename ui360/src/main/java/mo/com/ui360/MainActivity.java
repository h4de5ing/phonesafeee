package mo.com.ui360;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_ui;
    private ImageView iv_ui2;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        iv_ui = (ImageView) findViewById(R.id.iv_ui_360);
        iv_ui2 = (ImageView) findViewById(R.id.iv_ui2_360);

        ObjectAnimator oa = ObjectAnimator.ofFloat(iv_ui2, "rotation", new float[]{0,45,270, 360});
        oa.setDuration(1500);
        oa.setRepeatCount(1);
        oa.setRepeatMode(ObjectAnimator.REVERSE);
        oa.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        oa.start();
    }

}
