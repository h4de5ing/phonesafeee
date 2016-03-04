package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
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
 * <p/>
 * 黑名单的修改
 */


public class BlackUpdateActivity extends Activity {
    EditText bm_number;
    RadioGroup ba_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackupdate);
        initView();
        initData();
        initEvent();

    }

    private void initView() {
        bm_number = (EditText) findViewById(R.id.et_bm_number);
        ba_type = (RadioGroup) findViewById(R.id.rg_ba_type);
    }

    private void initData() {
        /*获取上一个界面带过来的数据*/
        Intent intent = getIntent();
        String pre_number = intent.getStringExtra(BlackManagerActivity.BLACK_NUMBER);
        int pre_type = intent.getIntExtra(BlackManagerActivity.BLACK_TYPE, BlackBean.TYPE_ALL);

        /*回显数据*/
        bm_number.setText(pre_number);

        int id = R.id.rg_ba_type_all; //默认的
        switch (pre_type) {
            case BlackBean.TYPE_ALL:
                id = R.id.rg_ba_type_all;
                break;
            case BlackBean.TYPE_SMS:
                id = R.id.rg_ba_type_sms;
                break;
            case BlackBean.TYPE_CALL:
                id = R.id.rg_ba_type_call;
                break;
        }
        ba_type.check(id);

    }

    /**
     * 事件的监听
     */
    private void initEvent() {
        //点击更新用户修改的黑名单
        findViewById(R.id.tb_ba_Update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBlack();
            }
        });

        /**
         * 用户取消操作
         */
        findViewById(R.id.tb_ba_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 修改黑名单
     */
    private void updateBlack() {
        String number = bm_number.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            bm_number.setError(getString(R.string.blacklistnumbernoempty));
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
        BlackDao dao = new BlackDao(BlackUpdateActivity.this);
        boolean insert = dao.update(number, type);
        if (insert) {
            Toast.makeText(BlackUpdateActivity.this, getString(R.string.changesuccess), Toast.LENGTH_SHORT).show();
            gotoBlackManger();
        } else {
            Toast.makeText(BlackUpdateActivity.this, getString(R.string.changefaild), Toast.LENGTH_SHORT).show();
            gotoBlackManger();
        }
    }

    /**
     * 回到黑名单管理界面
     */
    private void gotoBlackManger() {
        //Intent intent = new Intent(BlackUpdateActivity.this, BlackManagerActivity.class);
        //startActivity(intent);
        finish();
    }
}