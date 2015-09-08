package mo.com.phonesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import mo.com.phonesafe.dao.AddressDao;

/**
 * 号码归属地Dao测试
 */
public class AddressDaoTest extends ApplicationTestCase<Application> {
    private static final String TAG = "AddressDaoTest";

    public AddressDaoTest() {
        super(Application.class);
    }

    public void testQuery() {
        AddressDao dao = new AddressDao();
        String address = dao.getAddress(getContext(), "1557764");
        Log.i(TAG, "testQuery 号码归属地是：" + address);
    }
}