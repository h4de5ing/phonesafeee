package mo.com.phonesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
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
    EditText viewById;
    RadioGroup ba_tyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackall);

        //初始化view
        initView();

//        初始化event
        initEvent();

        //初始化数据
        initData();
    }

    private void initView() {

        viewById = (EditText) findViewById(R.id.et_bm_number);
        ba_tyle = (RadioGroup) findViewById(R.id.rg_ba_type);
    }

    private void initData() {
    }

    /**
     * 事件的监听
     */
    private void initEvent() {

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
                int checkedId = ba_tyle.getCheckedRadioButtonId();

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

                    //关闭当前页面
                    finish();
                } else {
                    Toast.makeText(BlackAddActivity.this, "添加黑名单失败", Toast.LENGTH_SHORT).show();
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
}