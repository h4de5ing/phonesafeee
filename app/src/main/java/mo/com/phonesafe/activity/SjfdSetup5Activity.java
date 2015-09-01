package mo.com.phonesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public class SjfdSetup5Activity extends SjfdSetupBaseActivity {
    private static final String TAG = "SjfdSetup1Activity";

    CheckBox sjfd_protect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup5);

        sjfd_protect = (CheckBox) findViewById(R.id.cb_sjfd_protect);

        //初始化回显数据
        boolean set_protect = PreferenceUtils.getBoolean(this, Constants.SJFD_PROTECT);


        if(set_protect){
            //已经开启手机防盗
            sjfd_protect.setChecked(true);
        }else{

            //没有开启手机防盗
            sjfd_protect.setChecked(false);
        }

        //设置开启防盗保护的事件 监听
        sjfd_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //被点击了
                    //已经开启手机防盗
                    sjfd_protect.setChecked(true);

                    //并将用户的选择保存到配置文件中
                    PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.SJFD_PROTECT, true);

                }else{
                    //取消
                    //没有开启手机防盗
                    sjfd_protect.setChecked(false);

                    //并将用户的选择保存到配置文件中
                    PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.SJFD_PROTECT, false);
                }
            }
        });


    }
    //有上一步
    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup4Activity.class);
        startActivity(intent);
        return false;
    }

    //执行下一步操作
    @Override
    protected boolean doNext() {

        //判断用户是否已经启动了手机防盗功能

        boolean isProtect = PreferenceUtils.getBoolean(this, Constants.SJFD_PROTECT);

        Log.d(TAG, "----------isProtect" + isProtect);
        if (!isProtect) {
            //没有开启,不能进行下一步的操作
            Toast.makeText(this, "您还未启动手机防盗功能", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Intent intent = new Intent(this, SjfdSetupActivity.class);
            startActivity(intent);
            return false;
        }
    }

}
