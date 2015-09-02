package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import mo.com.phonesafe.R;

/**
 * 作者：MoMxMo on 2015/8/31 19:59
 * 邮箱：momxmo@qq.com
 * <p/>
 * 黑名单管理的Activity
 */


public class BlackManagerActivity extends Activity {

    private ImageView lm_ivon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackmanager);

        //初始化view
        initView();

//        初始化event
        initEvent();

        //初始化数据
        initData();
    }

    private void initEvent() {
        lm_ivon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*进入黑名单添加界面*/
                Intent intent = new Intent(BlackManagerActivity.this,BlackAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
    }

    private void initView() {

        lm_ivon = (ImageView) findViewById(R.id.iv_lm_icon);



    }


}
