package mo.com.rocket;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class SmokeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke);
        ImageView smoke_t = (ImageView) findViewById(R.id.iv_smoke_t);
        ImageView smoke_m = (ImageView) findViewById(R.id.iv_smoke_m);

        //动画 透明度
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(smoke_t, "alpha", 0, 1);
        animator1.setDuration(600);
        animator1.setRepeatMode(ObjectAnimator.REVERSE);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.start();


        //动画 透明度
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(smoke_m, "alpha", 0, 1);
        animator2.setDuration(600);
        animator2.setRepeatMode(ObjectAnimator.REVERSE);
        animator2.start();

/*
        Animation animation_1 = AnimationUtils.loadAnimation(this,
                R.anim.anim_smoke_1);
        smoke_m.startAnimation(animation_1);
        smoke_t.startAnimation(animation_1);

        animation_1.

        Animation animation_2 = AnimationUtils.loadAnimation(this,
                R.anim.anim_smoke_2);
        smoke_m.startAnimation(animation_2);
        smoke_t.startAnimation(animation_2);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
