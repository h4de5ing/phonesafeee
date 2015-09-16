package mo.com.phonesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import mo.com.phonesafe.dao.AnitVirusDao;
import mo.com.phonesafe.tools.MD5Utils;

/**
 * 号码归属地Dao测试
 */
public class AnitVirusDaoTest extends ApplicationTestCase<Application> {
    private static final String TAG = "AnitVirusDaoTest";

    public AnitVirusDaoTest() {
        super(Application.class);
    }

    public void testinsert() {
        AnitVirusDao.insert(getContext(),"1b7be68dbb734319790859b2be58d428");
    }

    public void testMd5() {
        try {
            InputStream open = getContext().getAssets().open("ApiDemos.apk");
            String encode = MD5Utils.encode(open);
            Log.i(TAG, "testMd5 :"+encode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}