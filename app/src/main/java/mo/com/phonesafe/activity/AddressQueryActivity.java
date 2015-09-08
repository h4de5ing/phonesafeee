package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.dao.AddressDao;

/**
 * 号码归属地Activity
 */
public class AddressQueryActivity extends Activity {

    private EditText et_number;
    private Button bt_ok;
    private TextView tv_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);

        //初始化Vie
        initView();

        //事件的监听
        initEvent();
    }


    private void initView() {
        et_number = (EditText) findViewById(R.id.et_add_number);
        bt_ok = (Button) findViewById(R.id.bt_query_ok);
        tv_address = (TextView) findViewById(R.id.tv_address);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddressQueryActivity.this,CommonToolActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
        super.onBackPressed();
    }

    private void initEvent() {
        //点击查询
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Animation shake = AnimationUtils.loadAnimation(AddressQueryActivity.this, R.anim.shake);
                    et_number.startAnimation(shake);

                    Toast.makeText(AddressQueryActivity.this, "号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                AddressDao dao = new AddressDao();
                String address = dao.getAddress(AddressQueryActivity.this, number);
                if (address != null) {


                    tv_address.setText("号码归属地为："+address);
                } else {
                    tv_address.setText("您输入的不是地球的手机号码");
                }
            }
        });

    }

}
