package mo.com.phonesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.business.SmsProvider;
import mo.com.phonesafe.view.NormalItemView;

/**
 * 常用工具Activity
 */
public class CommonToolActivity extends Activity {

    private NormalItemView ct_address_que;
    private NormalItemView ct_normal_que;
    private NormalItemView mSms_backup;
    private NormalItemView mSms_restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);
        initView();
        initEvent();
    }

    private void initEvent() {

        //点击进入号码归属地查询
        ct_address_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preNextActivity(AddressQueryActivity.class);
            }
        });

        //点击进入常用号码查询
        ct_normal_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preNextActivity(NormalNumerActivity.class);
            }
        });

        //点击短信的备份
        mSms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UI进度的显示
                final ProgressDialog dialog = new ProgressDialog(CommonToolActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                //数据的加载到json格式文件中(使用内容提供者获取短信数据)
                SmsProvider.backup(CommonToolActivity.this, new SmsProvider.OnBackUpListener() {
                    @Override
                    public void onProgress(int progress, int max) {
                        dialog.setMax(max);
                        dialog.setProgress(progress);
                    }

                    @Override
                    public void onResult(boolean result) {
                        dialog.dismiss();
                        if (result) {
                            Toast.makeText(CommonToolActivity.this, getString(R.string.backupsuccess), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CommonToolActivity.this, getString(R.string.backupfaild), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        //查看备份短信
        mSms_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommonToolActivity.this, ViewBackupSmsActivity.class));
            }
        });
    }

    private void preNextActivity(Class clazz) {
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
    }
}
