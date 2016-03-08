package mo.com.phonesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import mo.com.phonesafe.R;

/**
 * Created by Gh0st on 2015/8/30 20:47
 */


public class SjfdSetup1Activity extends SjfdSetupBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup1);
    }

    @Override
    protected boolean doPre() {
        return true;
    }

    @Override
    protected boolean doNext() {
        Intent intent = new Intent(this, SjfdSetup2Activity.class);
        startActivity(intent);
        return false;
    }
}
