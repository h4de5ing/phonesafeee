package mo.com.phonesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

import mo.com.phonesafe.bean.BlackBean;
import mo.com.phonesafe.dao.BlackDao;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class BlackDBTest extends ApplicationTestCase<Application> {
    private static final String TAG = "BlackDBTest";

    public BlackDBTest() {
        super(Application.class);
    }

    public void testInsert() {
        BlackDao dao = new BlackDao(getContext());
        for (int i = 0; i < 80; i++) {
            boolean insert = dao.insert(5556+i + "", 2);
            assertEquals(insert, true);
        }
    }

    public void testupdate() {
        BlackDao dao = new BlackDao(getContext());
        boolean update = dao.update("5555", 1);
        assertEquals(update, true);
    }

    public void testDelete() {
        BlackDao dao = new BlackDao(getContext());

        for (int i = 0; i < 80; i++) {
            boolean delete = dao.delete(5556 + i + "");
            assertEquals(delete, true);
        }
    }

    public void testquery() {
        BlackDao dao = new BlackDao(getContext());
        BlackBean bean = dao.query("5555");
        Log.d(TAG, "testquery :" + bean.number + "......." + bean.type);
    }

    public void testqueryPageSize() {
        BlackDao dao = new BlackDao(getContext());
        List<BlackBean> list = dao.querySize(20, 1);
        for (BlackBean bean :
                list) {
            Log.d(TAG, "testquery :" + bean.number + "......." + bean.type);
        }

    }
    public void testfindType() {
        BlackDao dao = new BlackDao(getContext());
        int type = dao.getType("5555");
        Log.d(TAG, "testquery :"+type);
    }
}