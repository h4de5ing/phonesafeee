package mo.com.phonesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.business.SmsProvider;
import mo.com.phonesafe.service.AppLockDogService;
import mo.com.phonesafe.tools.ServiceStateUtils;
import mo.com.phonesafe.view.NormalItemView;
import mo.com.phonesafe.view.SettingItemView;

/**
 * 常用工具Activity
 */
public class CommonToolActivity extends Activity {

    private static final String TAG = "CommonToolActivity";
    private NormalItemView ct_address_que;
    private NormalItemView ct_normal_que;
    private NormalItemView mSms_backup;
    private NormalItemView mSms_restore;
    private NormalItemView si_appsocke_manager;
    private SettingItemView open_dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);
        initView();
        initEvent();
    }

    private void initEvent() {

        /**
         * 电子狗的点击监听事件
         */
        open_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isStart = ServiceStateUtils.getServiceState(CommonToolActivity.this, AppLockDogService.class);

                if (isStart) {
                    //点击关闭电子狗服务
                    Intent intent = new Intent(CommonToolActivity.this, AppLockDogService.class);
                    stopService(intent);
                } else {
                    //点击开启电子狗服务
                    Intent intent = new Intent(CommonToolActivity.this, AppLockDogService.class);
                    startService(intent);
                }

                //更新UI
                open_dog.setToggleState(!isStart);
            }
        });

        //点击进入号码归属地查询
        ct_address_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入
                preNextActivty(AddressQueryActivity.class);
            }
        });

        //点击进入常用号码查询
        ct_normal_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入
                preNextActivty(NormalNumerActivity.class);
            }
        });

        //点击短信的备份
        mSms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick 开始备份");

                //UI进度的显示
                final ProgressDialog dialog = new ProgressDialog(CommonToolActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   //设置进度条的显示风格样式
                dialog.setCanceledOnTouchOutside(false);    //设置外侧获取不到焦点
                dialog.show();

                //数据的加载到json格式文件中(使用内容提供者获取短信数据)
                SmsProvider.backup(CommonToolActivity.this, new SmsProvider.OnBackUpListener() {
                    @Override
                    public void onProgress(int progress, int max) {
                        //实现监听中的进度
                        dialog.setMax(max);
                        dialog.setProgress(progress);
                    }

                    @Override
                    public void onResult(boolean result) {
                        dialog.dismiss();
                        if (result) {
                            //备份成功
                            Toast.makeText(CommonToolActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //备份失败
                            Toast.makeText(CommonToolActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //点击进行短信的还原
        mSms_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick 开始还原短信");
                startActivity(new Intent(CommonToolActivity.this, ViewBackupSmsActivity.class));

                //UI进度的显示
                //final ProgressDialog dialog = new ProgressDialog(CommonToolActivity.this);
                //dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   //设置进度条的显示风格样式
                //dialog.setCanceledOnTouchOutside(false);    //设置外侧获取不到焦点
                //dialog.show();

                //数据的还原 真机测试权限短信还原代码 TODO
                /*SmsProvider.restore(CommonToolActivity.this, new SmsProvider.onRestoreListener() {
                    @Override
                    public void onProgress(int progress, int max) {
                        //监听进度
                        dialog.setProgress(progress);
                        dialog.setMax(max);
                    }

                    @Override
                    public void onResult(boolean result) {
                        //监听结果
                        dialog.dismiss();
                        if (result) {
                            Toast.makeText(CommonToolActivity.this, "短信还原成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CommonToolActivity.this, "短信还原失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
            }
        });

        /**
         * 点击进入程序锁管理
         */
        si_appsocke_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preNextActivty(AppLockManagerActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        open_dog.setToggleState(ServiceStateUtils.getServiceState(CommonToolActivity.this,
                AppLockDogService.class));
    }

    private void preNextActivty(Class clazz) {
        Intent intent = new Intent(CommonToolActivity.this, clazz);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
    }


    private void initView() {
        ct_address_que = (NormalItemView) findViewById(R.id.si_address_query);
        ct_normal_que = (NormalItemView) findViewById(R.id.si_normal_query);
        mSms_backup = (NormalItemView) findViewById(R.id.si_sms_backup);
        mSms_restore = (NormalItemView) findViewById(R.id.si_sms_restore);
        si_appsocke_manager = (NormalItemView) findViewById(R.id.si_appsocke_manager);
        open_dog = (SettingItemView) findViewById(R.id.siv_open_dog);
    }
}
