package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public class SjfdSetup3Activity extends SjfdSetupBaseActivity {
    private static final String TAG = "SjfdSetup3Activity";
    private static final int CONTACT_CODE_PHONE = 100;

    EditText sjfd_phone_number;
    RelativeLayout select_contace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup3);

        /*初始化view*/
        initView();

        //初始化监听事件
        initEvent();

        Log.i(TAG, "SjfdSetup3Activity//...........");
    }

    //初始化监听事件
    private void initEvent() {

//        用户点击选择联系人的事件
        select_contace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                进入ContactActivity选择联系人，并将结果返回
//                Intent intent = new Intent(SjfdSetup3Activity.this, ContactActivity.class);


                //使用大数据量的CursorAdater的方式去获取数据，减少内存的销毁，性能得到提高
                Intent intent = new Intent(SjfdSetup3Activity.this, ContactActivity2.class);

                startActivityForResult(intent, CONTACT_CODE_PHONE);
            }
        });
    }

    //接收返回的联系人数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONTACT_CODE_PHONE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //获取数据
                    String number = data.getStringExtra(ContactActivity.KEY_NUMBER);

                    Log.i(TAG, "...........number:" + number);
                    sjfd_phone_number.setText(number);

                    if (!TextUtils.isEmpty(number)) {
                        //设置光标的位置在number的后面
                        sjfd_phone_number.setSelection(number.length());
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化view
     */
    private void initView() {

        sjfd_phone_number = (EditText) findViewById(R.id.sjfd_et_phoneNumber);
        select_contace = (RelativeLayout) findViewById(R.id.select_contact);

        //将用户输入的号码进行保存
        String number = PreferenceUtils.getString(this, Constants.SJFD_PHONE_NUMBER);

        //回显用户输入的号码
        if (!TextUtils.isEmpty(number)) {
            sjfd_phone_number.setText(number);
            sjfd_phone_number.setSelection(number.length());
        }

    }

    //有上一步
    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup2Activity.class);
        startActivity(intent);
        return false;
    }

    //执行下一步操作
    @Override
    protected boolean doNext() {

        /*判断用户是否已经输入安全号码*/
        String phoneNumber = sjfd_phone_number.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            //用户没有输入
            Toast.makeText(this, "您还没有输入安全号码", Toast.LENGTH_SHORT).show();

            return true;
        } else {
            //将用户输入的号码进行保存
            PreferenceUtils.putString(this, Constants.SJFD_PHONE_NUMBER, phoneNumber);

            //用户进入下一步
            Intent intent = new Intent(this, SjfdSetup4Activity.class);
            startActivity(intent);
            return false;
        }
    }
}
