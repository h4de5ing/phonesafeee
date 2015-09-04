package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.service.CallSmsInterceptService;
import mo.com.phonesafe.tools.Constants;
import mo.com.phonesafe.tools.ServiceStateUtils;
import mo.com.phonesafe.view.SettingItemView;

/**
 * 作者：MoMxMo on 2015/8/29 21:18
 * 邮箱：xxxx@qq.com
 */


public class SettingActivity extends Activity {

    private static final String TAG = "SettingActivity";
    SettingItemView mSivAutoUpdate;
    SettingItemView mSivAutoIntercept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityi_setting);
        //初始化View
        initView();

        //初始化数据
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mSivAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "settingAutoUpdate");

                //UI图标开的时候就关，关的时候就开
                mSivAutoUpdate.toggle();

                //业务逻辑：下次进入时，不检测网络更新--》持久化存储
                PreferenceUtils.putBoolean(SettingActivity.this, Constants.AUTO_UPDATE, mSivAutoUpdate.getToggleState());

            }
        });


        //拦截服务点击事件的监听
        mSivAutoIntercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "拦截设置被点击了额");

                //UI图标开的时候就关，关的时候就开
                mSivAutoIntercept.toggle();
                //业务逻辑：下次进入时，不检测网络更新--》持久化存储

                if (ServiceStateUtils.getServiceState(SettingActivity.this, CallSmsInterceptService.class)) {
                    //服务是开启的，点击停止服务
                    Intent intent = new Intent(SettingActivity.this, CallSmsInterceptService.class);
                    stopService(intent);
                }else {
                    //服务没有开启，点击开启服务
                    Intent intent = new Intent(SettingActivity.this, CallSmsInterceptService.class);
                    startService(intent);
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //显示服务开启的状态(在onstart状态获取实时服务启动的状态)
        //设置拦截
        mSivAutoIntercept.setToggleState(ServiceStateUtils.getServiceState(this,CallSmsInterceptService.class));

    }

    //初始化View
    private void initView() {

        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
        mSivAutoIntercept = (SettingItemView) findViewById(R.id.setting_siv_autouIntercept);

        //设置初始化用户设置更新的状态
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE));

        Log.i(TAG, "initView 更新：" + PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE));
        Log.i(TAG, "initView 拦截："+PreferenceUtils.getBoolean(this, Constants.AUTO_INTERCEPT));
    }

    //点击返回主界面
    public void exitSetting(View view) {
        /*关闭自己的界面*/
        finish();
    }
}
