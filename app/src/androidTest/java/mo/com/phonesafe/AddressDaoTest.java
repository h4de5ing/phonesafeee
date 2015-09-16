package mo.com.phonesafe;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
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


    public void testA() {
        //还原到数据库中
        ContentResolver resolver = getContext().getContentResolver();
        Uri uri = Uri.parse("content://sms");
        ContentValues values = new ContentValues();
        values.put("address","123455");
        values.put("date","12434343545");
        values.put("body", "fefefffffffffffffffff");
        values.put("type", 1);
        Uri insert = resolver.insert(uri, values);
        Log.i(TAG, "------------------------ "+insert.toString());
    }


}