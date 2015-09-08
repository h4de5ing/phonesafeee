package mo.com.phonesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mo.com.phonesafe.tools.GZIPUtils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class GZIPTest extends ApplicationTestCase<Application> {
    private static final String TAG = "BlackDBTest";

    public GZIPTest() {
        super(Application.class);
    }

    public void testunzip() {
        final File file = new File(getContext().getFilesDir(), "address.db");
//        if (!file.exists()) {

            //耗时的操作放到子线程中
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = getContext().getAssets().open("address.zip");
//                        is = getContext().getResources().openRawResource(R.raw.address);
                        Log.i(TAG, "流的大小：" + (is == null));
                        os = new FileOutputStream(file);
                        GZIPUtils.unzip(is, os);
                    } catch (IOException e) {
                        //出现异常，删除解压产生的数据
                        file.delete();
                    }
                }
            }).start();

//        }
    }

}