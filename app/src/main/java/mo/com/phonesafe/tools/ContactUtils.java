package mo.com.phonesafe.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.bean.ContactInfo;

/**
 * 作者：MoMxMo on 2015/8/31 20:26
 * 邮箱：momxmo@qq.com
 * <p/>
 * 通过内容提供者获取系统中的联系人信息
 */


public class ContactUtils {


    /**
     * 获取手机中的所有联系人信息
     *
     * @return
     */
    public static List<ContactInfo> getAllPhone(Context context) {
        List<ContactInfo> list = null;

        ContentResolver contentResolver = context.getContentResolver();

//        使用google建议的标准的方式去获取匹配的Uri

        //这是获取手机联系人的Uri，通过这种方式可以尽量避免出错
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,    //姓名
                ContactsContract.CommonDataKinds.Phone.NUMBER,  //号码
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //联系人ID
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        list = new ArrayList<ContactInfo>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactInfo info = new ContactInfo();

                info.name = cursor.getString(0);
                info.number = cursor.getString(1);
                info.contactId = cursor.getLong(2);
                list.add(info);
            }
            //一定要记得关闭
            cursor.close();
        }
        return list;
    }

    /**
     * 获取手机联系人的头像图片
     */

    public static Bitmap getContactBitmap(Context context, long contact_id) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact_id + "");

        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    /**
     * 获取所有联系人信息，通过游标Cursor返回
     *
     * 提供给CursorAdater优化
     *
     * @param context
     * @return
     */
    public static Cursor getAllCursor(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

//        使用google建议的标准的方式去获取匹配的Uri

        //这是获取手机联系人的Uri，通过这种方式可以尽量避免出错
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,    //id
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,    //姓名
                ContactsContract.CommonDataKinds.Phone.NUMBER,  //号码
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //联系人ID
        };
        return   contentResolver.query(uri, projection, null, null, null);
    }

    /**
     * 获取一个联系人
     * @param cursor
     * @return
     */
    public static ContactInfo getContactInfo(Cursor cursor) {
        ContactInfo info = new ContactInfo();
        info.name = cursor.getString(1);
        info.number = cursor.getString(2);
        info.contactId = cursor.getLong(3);
        return info;
    }
}
