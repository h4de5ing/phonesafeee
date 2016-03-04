package mo.com.phonesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mo.com.phonesafe.bean.BlackBean;
import mo.com.phonesafe.db.DBHelper;
import mo.com.phonesafe.db.BlackListUtils;

/**
 * 作者：MoMxMo on 2015/9/2 22:26
 * 邮箱：xxxx@qq.com
 */


public class BlackDao {
    DBHelper dbHelper;

    public BlackDao(Context context) {
        dbHelper = new DBHelper(context);

    }

    /**
     * 增加黑名单
     */
    public boolean insert(String number, int type) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long insert = 0;
        if (db.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BlackListUtils.BlackTable.COLUMN_NUMBER, number);
            contentValues.put(BlackListUtils.BlackTable.COLUMN_TYPE, type);
            insert = db.insert(BlackListUtils.BlackTable.TABLE_NAME, null, contentValues);
        }
        db.close();
        return insert != -1;

    }

    public boolean delete(String number) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delete = 0;
        if (db.isOpen()) {
            String where = BlackListUtils.BlackTable.COLUMN_NUMBER + "=?";

            delete = db.delete(BlackListUtils.BlackTable.TABLE_NAME, where, new String[]{number});
        }
        db.close();
        return delete > 0;
    }

    public boolean update(String number, int type) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int update = 0;
        if (db.isOpen()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BlackListUtils.BlackTable.COLUMN_TYPE, type);
            String where = BlackListUtils.BlackTable.COLUMN_NUMBER + "=?";
            update = db.update(BlackListUtils.BlackTable.TABLE_NAME, contentValues, where, new String[]{number});
        }
        db.close();
        return update > 0;
    }

    public BlackBean query(String number) {
        BlackBean bean = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            String[] columns = {BlackListUtils.BlackTable.COLUMN_NUMBER, BlackListUtils.BlackTable.COLUMN_TYPE};
            String select = BlackListUtils.BlackTable.COLUMN_NUMBER + "=?";
            String[] args = {number};
            Cursor cursor = db.query(BlackListUtils.BlackTable.TABLE_NAME, columns, select, args, null, null, null);
            if (cursor != null) {
                bean = new BlackBean();
                cursor.moveToFirst();
                String dbnumber = cursor.getString(0);
                int type = cursor.getInt(1);
                bean.number = dbnumber;
                bean.type = type;
            }
            cursor.close();
        }
        db.close();
        return bean;
    }

    /**
     * 获取所有黑名单
     *
     * @return
     */
    public List<BlackBean> queryAll() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BlackBean> list = null;
        if (db.isOpen()) {
            String[] columns = {BlackListUtils.BlackTable.COLUMN_NUMBER, BlackListUtils.BlackTable.COLUMN_TYPE};
            Cursor cursor = db.query(BlackListUtils.BlackTable.TABLE_NAME, columns, null, null, null, null, null);
            if (cursor != null) {
                list = new ArrayList<BlackBean>();
                while (cursor.moveToNext()) {
                    BlackBean bean = new BlackBean();
                    String number = cursor.getString(0);
                    int type = cursor.getInt(1);
                    bean.number = number;
                    bean.type = type;
                    list.add(bean);
                }
                cursor.close();
            }
        }
        db.close();
        return list;
    }

    /**
     * 分页获取黑名单
     *
     * @return
     */
    public List<BlackBean> querySize(int size, int index) {
        /*sql语句：select * from black limit 20 offset 3;*/
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BlackBean> list = null;
        //if (db.isOpen()) {
            String[] columns = {BlackListUtils.BlackTable.COLUMN_NUMBER, BlackListUtils.BlackTable.COLUMN_TYPE};
            String selection = "limit ? offset ?";
            String[] selectionArgs = {size + "", index + ""};

            String sql = "select " +
                    BlackListUtils.BlackTable.COLUMN_NUMBER+","+
                    BlackListUtils.BlackTable.COLUMN_TYPE+
                    " from "
                    + BlackListUtils.BlackTable.TABLE_NAME
                    + " limit " + size + " offset " + index;

            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                list = new ArrayList<BlackBean>();
                while (cursor.moveToNext()) {
                    BlackBean bean = new BlackBean();
                    String number = cursor.getString(0);
                    int type = cursor.getInt(1);
                    bean.number = number;
                    bean.type = type;
                    list.add(bean);
                }
                cursor.close();
            }
       // }
        db.close();
        return list;
    }

    /**
     * 获取黑名单中的拦截类型
     *
     * @param number
     * @return 返回-1 表示不在黑名单中
     */
    public int getType(String number) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int type = -1;
        if (db.isOpen()) {
            String[] colums = {BlackListUtils.BlackTable.COLUMN_TYPE};
            String selection = BlackListUtils.BlackTable.COLUMN_NUMBER + "=?";
            String[] selectionArgs = {number};

            Cursor cursor = db.query(BlackListUtils.BlackTable.TABLE_NAME, colums, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                type = cursor.getInt(cursor.getColumnIndex(BlackListUtils.BlackTable.COLUMN_TYPE));
            }
        }
        return type;
    }
}
