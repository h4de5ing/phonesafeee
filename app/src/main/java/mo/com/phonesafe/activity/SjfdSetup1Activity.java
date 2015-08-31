package mo.com.phonesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import mo.com.phonesafe.R;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public class SjfdSetup1Activity extends SjfdSetupBaseActivity {
    private static final String TAG = "SjfdSetup1Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup1);
        Log.i(TAG, "SjfdSetup1Activity//...........");
    }

    //没有上一步
    @Override
    protected boolean doPre() {
        return true;
    }

    //执行下一步操作
    @Override
    protected boolean doNext() {
        Intent intent = new Intent(this, SjfdSetup2Activity.class);
        startActivity(intent);
        return false;
    }
}
