package mo.com.phonesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import mo.com.phonesafe.R;

/**
 * 作者：MoMxMo on 2015/8/30 20:47
 * 邮箱：xxxx@qq.com
 */


public class SjfdSetup5Activity extends SjfdSetupBaseActivity {
    private static final String TAG = "SjfdSetup1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup5);
        Log.i(TAG, "SjfdSetup1Activity//...........");
    }
    //有上一步
    @Override
    protected boolean doPre() {
        Intent intent = new Intent(this, SjfdSetup4Activity.class);
        startActivity(intent);
        return false;
    }

    //执行下一步操作
    @Override
    protected boolean doNext() {
        Intent intent = new Intent(this, SjfdSetupActivity.class);
        startActivity(intent);
        return true;
    }

}
