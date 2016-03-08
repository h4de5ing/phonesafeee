package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.service.AppLockDogService;

/**
 * 程序锁界面  独立的任务栈
 */
public class LockScreenActivity extends Activity {

    private Button btn_ok;
    private EditText et_password;
    private TextView lock_title;
    private ImageView lock_icon;
    public static final String FREED_PACKNAME="freed_packname";
    private String packageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        initView();
        initEvent();
        initData();
    }


    private void initData() {

        //获取启动者传送管理的数据
        packageName = getIntent().getStringExtra(AppLockDogService.PCK_NAME);

        /*使用包管理器获取应用程序的信息*/
        PackageManager pm = getPackageManager();

        try {

            //得到应用程序的信息
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);

            //应用程序的logo
            Drawable drawable = info.loadIcon(pm);
            lock_icon.setImageDrawable(drawable);

            //获取应用程序的名称
            String appName = info.loadLabel(pm).toString();
            lock_title.setText(appName);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void initView() {
        btn_ok = (Button) findViewById(R.id.btn_OK);
        et_password = (EditText) findViewById(R.id.et_password);
        lock_title = (TextView) findViewById(R.id.iv_lock_title);
        lock_icon = (ImageView) findViewById(R.id.iv_lock_icon);
    }

    private void initEvent() {

        //点击确定按钮
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LockScreenActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals("123")) {
                    Toast.makeText(LockScreenActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*显示对应锁的应用*/
                Intent intent = new Intent();
                intent.setAction("mo.com.phonesafe.free");
                intent.putExtra(FREED_PACKNAME, packageName);
                sendBroadcast(intent);

                //密码正确，关闭当前页面
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 进入launcher界面
        // <intent-filter>
        // <action android:name="android.intent.action.MAIN" />
        // <category android:name="android.intent.category.HOME" />
        // <category android:name="android.intent.category.DEFAULT" />
        // <category android:name="android.intent.category.MONKEY"/>
        // </intent-filter>

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);

        finish();

    }
}
