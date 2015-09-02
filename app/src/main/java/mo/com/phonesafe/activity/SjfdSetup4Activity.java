package mo.com.phonesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.receiver.SjfdAdminReceicer;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public class SjfdSetup4Activity extends SjfdSetupBaseActivity {
    private static final String TAG = "SjfdSetup1Activity";
    private static final int REQUEST_CODE_ENABLE_ADMIN = 100;

    ComponentName whoN;
    DevicePolicyManager dpm;
    ImageView admin_icon;
    RelativeLayout admin_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup4);

        //初始化VIew
        initView();

        //初始化监听事件
        initEvent();

        Log.i(TAG, "SjfdSetup1Activity//...........");
    }

    private void initEvent() {
        admin_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断用户当前是否已经激活
                if (isAdminActive()) {
                    //已经激活，点击之后取消激活
                    dpm.removeActiveAdmin(whoN);
                    //UI显示
                    admin_icon.setImageResource(R.mipmap.eg);
                }else {
                    /*还没有激活*/

                    // Launch the activity to have the user enable our admin.
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, whoN);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
                }
            }
        });
    }

    /**
     * 获取用户是否已经点击激活了设备管理员功能
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //用已经点击激活设备管理员
                    //UI显示
                    admin_icon.setImageResource(R.mipmap.i);

                    Log.i(TAG, "onActivityResult 用户激活了设备管理员。。。。。。。。。。。");
                    break;
                case Activity.RESULT_CANCELED:
                    //用户取消点击激活
                    break;
            }
        }
    }

    private void initView() {
        admin_icon = (ImageView) findViewById(R.id.set_iv_admin);
        admin_rl = (RelativeLayout) findViewById(R.id.set_rl_admin);

        //判断用户当前是否已经激活
        if (isAdminActive()) {
            //UI显示
            admin_icon.setImageResource(R.mipmap.i);
        }else {
            //UI显示
            admin_icon.setImageResource(R.mipmap.eg);

        }
    }

    //有上一步
    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup3Activity.class);
        startActivity(intent);
        return false;
    }

    //执行下一步操作
    @Override
    protected boolean doNext() {
        boolean adminActive = isAdminActive();

        if (!adminActive) {
            /*用户没有激活*/
            Toast.makeText(SjfdSetup4Activity.this, "请激活设备管理员...", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Intent intent = new Intent(this, SjfdSetup5Activity.class);
            startActivity(intent);
            return false;
        }

    }

    /**
     * //判断用户是否已经激活设备设备管理员
     * @return
     */
    private boolean isAdminActive() {

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        whoN = new ComponentName(SjfdSetup4Activity.this,SjfdAdminReceicer.class);
        return dpm.isAdminActive(whoN);
    }


}
