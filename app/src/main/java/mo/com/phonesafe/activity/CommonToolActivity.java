package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import mo.com.phonesafe.R;
import mo.com.phonesafe.view.SettingItemView;

/**
 * 常用工具Activity
 */
public class CommonToolActivity extends Activity {

    private SettingItemView ct_address_que;
    private SettingItemView ct_normal_que;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);

        //初始化Vie
        initView();

        //事件的监听
        initEvent();
    }


    private void initEvent() {

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
    }

    private void preNextActivty(Class clazz) {
        Intent intent = new Intent(CommonToolActivity.this,clazz);
        startActivity(intent);
        //左右切入
        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
        finish();
    }


    private void initView() {
        ct_address_que = (SettingItemView) findViewById(R.id.si_address_query);
        ct_normal_que = (SettingItemView) findViewById(R.id.si_normal_query);
    }

}
