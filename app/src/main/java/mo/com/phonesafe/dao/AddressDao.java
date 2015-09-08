package mo.com.phonesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;

/**
 * 号码归属地数据库DAO
 * <p/>
 * 作者：MoMxMo on 2015/9/5 14:08
 * 邮箱：xxxx@qq.com
 */


public class AddressDao {

    /**
     * 通过号码获取归属地
     * @param context
     * @param number
     * @return      返回归属地，null表示没有
     */
    public static String getAddress(Context context, String number) {
        File file = new File(context.getFilesDir(), "address.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        String address = "";


        //手机号码正则表达式
        String match = "^1[34578]\\d{9}$";
        if (number.matches(match)) {
            String prefix = number.substring(0, 7);
            if (db.isOpen()) {
                String table_name = "info";
                String[] columns = {"cardtype"};
                String selection = "mobileprefix=?";
                String[] selectionArgs = {prefix};
                Cursor cursor = db.query(table_name, columns, selection, selectionArgs, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        address = cursor.getString(0);
                    }
                    cursor.close();
                }
            }
        } else {        //手机号码长度的匹配
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 5:
                    address = "服务电话";
                    break;
                case 7:     //电话座机
                case 8:
                    address = "本地座机";
                    break;
                case 10:
                case 11:
                case 12:        //以区号查询
                    if (db.isOpen()) {
                        String prefix = number.substring(0, 3);     //取3位
                        String table_name = "info";
                        String[] columns = {"city"};
                        String selection = "area=?";
                        String[] selectionArgs = {prefix};
                        Cursor cursor = db.query(table_name, columns, selection, selectionArgs, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                        if (TextUtils.isEmpty(address)) {
                            prefix = number.substring(0, 4);     //取4位
                            if (db.isOpen()) {
                                selectionArgs = new String[]{prefix};
                                cursor = db.query(table_name, columns, selection, selectionArgs, null, null, null);
                                if (cursor != null) {
                                    if (cursor.moveToNext()) {
                                        address = cursor.getString(0);
                                    }
                                    cursor.close();
                                }
                            }
                        }
                        if (TextUtils.isEmpty(address)) {
                            address = "未知号码";
                        }
                    }
                    break;

                default:
                    address = "未知号码";
                    break;
            }
        }
        db.close();
        return address;
    }


}
