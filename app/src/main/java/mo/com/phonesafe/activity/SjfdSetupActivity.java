package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * Created by Gh0st on 2015/8/30 20:47
 */


public class SjfdSetupActivity extends Activity {

    RelativeLayout enter_setting;
    RelativeLayout sjfd_protect;
    TextView tv_number;
    ImageView iv_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup);
        initView();
        initEvent();
    }

    private void initEvent() {
        enter_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SjfdSetupActivity.this, SjfdSetup1Activity.class);
                startActivity(intent);
            }
        });
        sjfd_protect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sjfd_protect = PreferenceUtils.getBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT);
                if (sjfd_protect) {
                    iv_icon.setImageResource(R.mipmap.unlock);
                    PreferenceUtils.putBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT,false);
                }else{
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
        String number = PreferenceUtils.getString(SjfdSetupActivity.this, Constants.SJFD_PHONE_NUMBER);
        tv_number.setText(number);
        boolean sjfd_protect = PreferenceUtils.getBoolean(SjfdSetupActivity.this, Constants.SJFD_PROTECT);
        if (sjfd_protect) {
            iv_icon.setImageResource(R.mipmap.lock);
        }else{
            iv_icon.setImageResource(R.mipmap.unlock);
        }
    }
}
