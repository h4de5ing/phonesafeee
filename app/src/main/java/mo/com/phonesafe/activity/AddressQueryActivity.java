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
        initView();
        initEvent();
    }


    private void initView() {
        et_number = (EditText) findViewById(R.id.et_add_number);
        bt_ok = (Button) findViewById(R.id.bt_query_ok);
        tv_address = (TextView) findViewById(R.id.tv_address);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddressQueryActivity.this, CommonToolActivity.class);
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
                    et_number.setError("号码不能为空");
                    return;
                }
/*                String url = "http://www.gpsspg.com/phone/?q=" + number;
                OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        tv_address.setText(response);
                    }
                });*/

                AddressDao dao = new AddressDao();
                String address = dao.getAddress(AddressQueryActivity.this, number);
                    tv_address.setText("号码归属地为：" + address);
            }
        });

    }

}
