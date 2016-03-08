package mo.com.phonesafe.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.db.AppLockDB;
import mo.com.phonesafe.db.AppLockDBHelper;

/**
 * Created by Gh0st on 2015/9/13 09:37
 */


public class AppLockDao {
    private AppLockDBHelper mHelper;
    private Context mContext;

    public AppLockDao(Context context) {
        mContext = context;
        mHelper = new AppLockDBHelper(context);
    }


    /**
     * 判断包名是否存在于数据库中
     *
     * @param pckName
     * @return
     */
    public boolean findLock(String pckName) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db.isOpen()) {
            String selection = AppLockDB.ApplockTable.PACKE_NAME + "=?";
            Cursor cursor = db.query(AppLockDB.ApplockTable.TABL_NAME, null, selection, new String[]{pckName}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    return true;
                }
            }
        }
        db.close();
        return false;
    }

    /**
     * 添加程序的包名到数据库中
     *
     * @param packageName
     * @return
     */
    public boolean insert(String packageName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long insert = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(AppLockDB.ApplockTable.PACKE_NAME, packageName);
            insert = db.insert(AppLockDB.ApplockTable.TABL_NAME, null, values);
        }
        db.close();

        /*使用内容解析者通知数据库发生了改变*/
        ContentResolver resolver = mContext.getContentResolver();
        resolver.notifyChange(Uri.parse("content://applock"), null);
        return insert != -1;
    }

    /**
     * 将程序的包名从数据库删除
     *
     * @param packageName
     * @return
     */
    public boolean delete(String packageName) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int delete = 0;
        if (db.isOpen()) {
            String whereClause = AppLockDB.ApplockTable.PACKE_NAME + "=?";
            String[] whereArg = {packageName};
            delete = db.delete(AppLockDB.ApplockTable.TABL_NAME, whereClause, whereArg);
        }
        db.close();

        /*使用内容解析者通知数据库发生了改变*/
        ContentResolver resolver = mContext.getContentResolver();
        resolver.notifyChange(Uri.parse("content://applock"), null);
        return delete > 0;
    }

    /**
     * 获取所有添加 了程序锁的包名
     *
     * @return
     */
    public List<String> getAllLockPackName() {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        List<String> list = null;
        if (db.isOpen()) {

            String[] columns = {AppLockDB.ApplockTable.PACKE_NAME};
            Cursor cursor = db.query(AppLockDB.ApplockTable.TABL_NAME, columns, null, null, null, null, null);

            if (cursor != null) {
                list = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    String packageName = cursor.getString(0);
                    list.add(packageName);
                }
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 获取已经加锁的程序包名数量
     *
     * @return
     */
    public int getCount() {
        String sql = "select count(1) from " + AppLockDB.ApplockTable.TABL_NAME;
        int count = 0;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
            }
        }
        db.close();
        return count;
    }
}
