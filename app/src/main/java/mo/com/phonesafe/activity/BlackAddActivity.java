package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.bean.BlackBean;
import mo.com.phonesafe.dao.BlackDao;

/**
 * 作者：MoMxMo on 2015/9/2 20:33
 * 邮箱：xxxx@qq.com
 */


public class BlackAddActivity extends Activity {
    private static final int CONTACT_CODE_PHONE = 100;
    private static final String TAG = "BlackAddActivity";
    EditText viewById;
    RadioGroup ba_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackall);
        initView();
        initEvent();
    }

    private void initView() {
        viewById = (EditText) findViewById(R.id.et_bm_number);
        ba_type = (RadioGroup) findViewById(R.id.rg_ba_type);
    }

    /**
     * 事件的监听
     */
    private void initEvent() {

        /**
         * 通讯录中获取名单，添加到黑名单中
         */
        findViewById(R.id.select_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入联系人选取号码中，使用On
                Intent intent = new Intent(BlackAddActivity.this, ContactActivity2.class);
                startActivityForResult(intent, CONTACT_CODE_PHONE);
            }
        });

        //确定
        findViewById(R.id.tb_ba_OK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = viewById.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(BlackAddActivity.this, "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取被点击的radio 的ID
                int checkedId = ba_type.getCheckedRadioButtonId();

                int type = BlackBean.TYPE_CALL;
                //用户选择的类型
                switch (checkedId) {
                    case R.id.rg_ba_type_call:      //电话
                        type = BlackBean.TYPE_CALL;
                        break;
                    case R.id.rg_ba_type_sms:      //短信
                        type = BlackBean.TYPE_SMS;
                        break;
                    case R.id.rg_ba_type_all:      //电话和短信
                        type = BlackBean.TYPE_ALL;
                        break;
                    default:
                        break;
                }

                //向数据库中添加黑名单
                BlackDao dao = new BlackDao(BlackAddActivity.this);
                boolean insert = dao.insert(number, type);
                if (insert) {
                    Toast.makeText(BlackAddActivity.this, "成功添加黑名单", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BlackAddActivity.this, "已经存在,添加黑名单失败", Toast.LENGTH_SHORT).show();
                    //关闭当前页面
                    finish();
                }

            }
        });

        findViewById(R.id.tb_ba_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭当前页面
                finish();
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
                    viewById.setText(number);

                    if (!TextUtils.isEmpty(number)) {
                        //设置光标的位置在number的后面
                        viewById.setSelection(number.length());
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        Intent intent = new Intent(BlackAddActivity.this, BlackManagerActivity.class);
        startActivity(intent);
        //关闭当前页面
        super.finish();
    }
}