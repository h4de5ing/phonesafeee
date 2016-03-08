package mo.com.phonesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * Created by Gh0st on 2015/8/30 20:47
 */


public class SjfdSetup5Activity extends SjfdSetupBaseActivity {

    private CheckBox sjfd_protect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup5);
        sjfd_protect = (CheckBox) findViewById(R.id.cb_sjfd_protect);
        boolean set_protect = PreferenceUtils.getBoolean(this, Constants.SJFD_PROTECT);


        if (set_protect) {
            sjfd_protect.setChecked(true);
        } else {
            sjfd_protect.setChecked(false);
        }

        //设置开启防盗保护的事件 监听
        sjfd_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sjfd_protect.setChecked(true);
                    PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.SJFD_PROTECT, true);
                } else {
                    sjfd_protect.setChecked(false);
                    PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.SJFD_PROTECT, false);
                }
            }
        });
    }

    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup4Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean doNext() {
        boolean isProtect = PreferenceUtils.getBoolean(this, Constants.SJFD_PROTECT);
        if (!isProtect) {
            Toast.makeText(this, getString(R.string.noactivitylost), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Intent intent = new Intent(this, SjfdSetupActivity.class);
            startActivity(intent);
            return false;
        }
    }

}
