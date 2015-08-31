package mo.com.phonesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 * <p/>
 * 绑定SIM卡
 */


public class SjfdSetup2Activity extends SjfdSetupBaseActivity {
    private static final String TAG = "SjfdSetup2Activity";

    ImageView bingIcon;
    RelativeLayout setup_bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup2);
        Log.i(TAG, "SjfdSetup2Activity//...........");

        //初始化View
        initView();

        //初始化事件
        initEvent();


    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        setup_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取SIM的标识码

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String simSerialNumber = tm.getSimSerialNumber();

                Log.i(TAG, "手机SIM的标识：" + simSerialNumber);

                //获取配置文件中的SIM绑定信息
                String sim = PreferenceUtils.getString(SjfdSetup2Activity.this, Constants.SJFD_SIM);

                if (TextUtils.isEmpty(sim)) {
                    //SIM没有绑定
                    bingIcon.setImageResource(R.mipmap.lock);

                    //将标识码写入配置文件中
                    PreferenceUtils.putString(SjfdSetup2Activity.this, Constants.SJFD_SIM, simSerialNumber);
                } else {
                    //取消绑定SIM
                    bingIcon.setImageResource(R.mipmap.unlock);

                    //将配置文件中的标识码去除
                    PreferenceUtils.putString(SjfdSetup2Activity.this, Constants.SJFD_SIM, null);
                }
            }
        });

    }

    /**
     * 初始化view
     */
    private void initView() {

        setup_bind = (RelativeLayout) findViewById(R.id.setup2_rl_bind);
        bingIcon = (ImageView) findViewById(R.id.setup_bing_icon);


        //获取SIM的标识码
        String sim = PreferenceUtils.getString(this, Constants.SJFD_SIM);
        /*回显图片数据*/
        bingIcon.setImageResource(TextUtils.isEmpty(sim) ? R.mipmap.unlock : R.mipmap.lock);

    }

    //有上一步
    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup1Activity.class);
        startActivity(intent);
        return false;
    }

    //执行下一步操作
    @Override
    protected boolean doNext() {

        //获取配置文件中的SIM绑定信息
        String sim = PreferenceUtils.getString(SjfdSetup2Activity.this, Constants.SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {

            /*如果用户没有绑定SIM卡，不允许进入下一步*/
            Toast.makeText(this,"您还未绑定SIM,请绑定。。。",Toast.LENGTH_SHORT).show();

            //不执行下一步的操作
            return true;
        }
        Intent intent = new Intent(this, SjfdSetup3Activity.class);
        startActivity(intent);
        return false;
    }


}
