package mo.com.phonesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

import mo.com.phonesafe.dao.AppLockDao;

/**
 * 号码归属地Dao测试
 */
public class AppLockDaoTest extends ApplicationTestCase<Application> {
    private static final String TAG = "AppLockDaoTest";

    public AppLockDaoTest() {
        super(Application.class);
    }

    public void testInsert() {
        AppLockDao dao = new AppLockDao(getContext());
        boolean insert = dao.insert("xxxx.xxx.app");
        if (insert) {
            Log.i(TAG, "testQuery 成功");
        } else {
            Log.i(TAG, "testQuery 失败");

        }
    }

    public void testQuery() {
        AppLockDao dao = new AppLockDao(getContext());
        List<String> list = dao.getAllLockPackName();
        for (String name : list) {
            Log.i(TAG, "成功" + name);
        }
    }

    public void testDelete() {
        AppLockDao dao = new AppLockDao(getContext());
        boolean delete = dao.delete("xxxx.xxx.app");
        if (delete) {
            Log.i(TAG, "testQuery 成功");
        } else {
            Log.i(TAG, "testQuery 失败");

        }
    }

    public void testgetCount() {
        AppLockDao dao = new AppLockDao(getContext());
        int count = dao.getCount();
        Log.i(TAG, "testgetCount 成功" + count);
    }
}

