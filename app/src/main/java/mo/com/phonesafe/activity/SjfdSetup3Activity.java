package mo.com.phonesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import mo.com.phonesafe.R;
import mo.com.phonesafe.preference.PreferenceUtils;
import mo.com.phonesafe.tools.Constants;

/**
 * Created by Gh0st on 2015/8/30 20:47
 */


public class SjfdSetup3Activity extends SjfdSetupBaseActivity {
    private static final int CONTACT_CODE_PHONE = 100;

    EditText sjfd_phone_number;
    RelativeLayout select_contace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup3);
        initView();
        initEvent();
    }

    private void initEvent() {
        select_contace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用大数据量的CursorAdapter的方式去获取数据，减少内存的销毁，性能得到提高
                Intent intent = new Intent(SjfdSetup3Activity.this, ContactActivity2.class);
                startActivityForResult(intent, CONTACT_CODE_PHONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONTACT_CODE_PHONE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String number = data.getStringExtra(ContactActivity.KEY_NUMBER);
                    sjfd_phone_number.setText(number);
                    if (!TextUtils.isEmpty(number)) { //设置光标的位置在number的后面
                        sjfd_phone_number.setSelection(number.length());
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        sjfd_phone_number = (EditText) findViewById(R.id.sjfd_et_phoneNumber);
        select_contace = (RelativeLayout) findViewById(R.id.select_contact);
        String number = PreferenceUtils.getString(this, Constants.SJFD_PHONE_NUMBER);
        if (!TextUtils.isEmpty(number)) {
            sjfd_phone_number.setText(number);
            sjfd_phone_number.setSelection(number.length());
        }
    }

    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup2Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected boolean doNext() {
        String phoneNumber = sjfd_phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, getString(R.string.inputsafenumber), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            PreferenceUtils.putString(this, Constants.SJFD_PHONE_NUMBER, phoneNumber);
            Intent intent = new Intent(this, SjfdSetup4Activity.class);
            startActivity(intent);
            return false;
        }
    }
}
