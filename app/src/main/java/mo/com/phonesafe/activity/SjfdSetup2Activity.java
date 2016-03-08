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
 * Created by Gh0st on 2015/8/30 20:47
 * <p/>
 * 绑定SIM卡
 */


public class SjfdSetup2Activity extends SjfdSetupBaseActivity {

    ImageView bingIcon;
    RelativeLayout setup_bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup2);
        initView();
        initEvent();
    }

    private void initEvent() {
        setup_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String simSerialNumber = tm.getSimSerialNumber();
                String sim = PreferenceUtils.getString(SjfdSetup2Activity.this, Constants.SJFD_SIM);
                if (TextUtils.isEmpty(sim)) {
                    bingIcon.setImageResource(R.mipmap.lock);
                    PreferenceUtils.putString(SjfdSetup2Activity.this, Constants.SJFD_SIM, simSerialNumber);
                } else {
                    bingIcon.setImageResource(R.mipmap.unlock);
                    PreferenceUtils.putString(SjfdSetup2Activity.this, Constants.SJFD_SIM, null);
                }
            }
        });

    }

    private void initView() {
        setup_bind = (RelativeLayout) findViewById(R.id.setup2_rl_bind);
        bingIcon = (ImageView) findViewById(R.id.setup_bing_icon);
        String sim = PreferenceUtils.getString(this, Constants.SJFD_SIM);
        bingIcon.setImageResource(TextUtils.isEmpty(sim) ? R.mipmap.unlock : R.mipmap.lock);
    }

    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup1Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean doNext() {
        String sim = PreferenceUtils.getString(SjfdSetup2Activity.this, Constants.SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, getString(R.string.tipnobind), Toast.LENGTH_SHORT).show();
            return true;
        }
        Intent intent = new Intent(this, SjfdSetup3Activity.class);
        startActivity(intent);
        return false;
    }


}
