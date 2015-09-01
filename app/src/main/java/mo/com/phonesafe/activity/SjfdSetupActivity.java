package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public class SjfdSetupActivity extends Activity {
    private static final String TAG = "SjfdSetup1Activity";

    RelativeLayout enter_setting;
    RelativeLayout sjfd_protect;
    TextView tv_number;
    ImageView iv_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup);

        //初始化View
        initView();


        //初始化事件
        initEvent();

        Log.i(TAG, "SjfdSetup1Activity...........");
    }

    private void initEvent() {
        enter_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "重新进入手机向导................. ");
                //重新进入手机向导
                Intent intent = new Intent(SjfdSetupActivity.this, SjfdSetup1Activity.class);
                startActivity(intent);
            }
        });
        sjfd_protect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "sjfd_protect................. ");

                //显示用户是否已经开启保护
                boolean sjfd_protect = PreferenceUtils.getBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT);
                if (sjfd_protect) {
                    //如果已经启动，点击之后关闭启动
                    iv_icon.setImageResource(R.mipmap.unlock);
                    PreferenceUtils.putBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT,false);

                }else{
                    //如果没有启动，点击之后启动
                    iv_icon.setImageResource(R.mipmap.lock);
                    PreferenceUtils.putBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT,true);

                }

            }
        });
    }


    private void initView() {
        enter_setting = (RelativeLayout) findViewById(R.id.rl_enter_setting);
        sjfd_protect = (RelativeLayout) findViewById(R.id.rl_sjfd_protect);
        tv_number = (TextView) findViewById(R.id.tv_sjfd_number);
        iv_icon = (ImageView)findViewById(R.id.iv_setup_icon);

        //显示安全号码
        String number = PreferenceUtils.getString(SjfdSetupActivity.this, Constants.SJFD_PHONE_NUMBER);
        tv_number.setText(number);

        //显示用户是否已经开启保护
        boolean sjfd_protect = PreferenceUtils.getBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT);
        if (sjfd_protect) {
            //用户未开启
            //UI显示未上锁
            iv_icon.setImageResource(R.mipmap.lock);
        }else{
            //用户开启
            iv_icon.setImageResource(R.mipmap.unlock);
        }
    }
}
