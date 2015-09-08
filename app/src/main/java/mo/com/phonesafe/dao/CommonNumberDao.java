package mo.com.phonesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * 作者：MoMxMo on 2015/9/7 20:44
 * 邮箱：xxxx@qq.com
 *
 *
 * 常用号码查询
 */


public class CommonNumberDao {

    //获取Group条目数量
    public static int getGroupConut(Context context) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

        int count = 0;
        if (db.isOpen()) {
            String sql = "select count(1) from classlist";
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


    /**
     * 获取子条目数量
     * @param context
     * @param groupPosition
     * @return
     */
    public static int getChildrenConut(Context context, int groupPosition) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

        int count = 0;
        if (db.isOpen()) {
            String sql = "select count(1) from table"+(groupPosition+1);
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
    /**
     * 获取Group的内容
     * @param context
     * @param groupPosition
     * @return
     */
    public static String getGroupContent(Context context, int groupPosition) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

        String content = "";
        if (db.isOpen()) {
            String sql = "select name from classlist where idx="+(groupPosition+1);
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    content = cursor.getString(0);
                }
                cursor.close();
            }
        }
        db.close();
        return content;
    }


    /**
     * 获取Child的内容
     * @param context
     * @param childPosition
     * @return
     */
    public static String getChildContent(Context context,int groupPosition, int childPosition) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

        String content = "";
        if (db.isOpen()) {
            String sql = "select name,number from table"+(groupPosition+1)+" where _id="+(childPosition+1);
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String name  = cursor.getString(0);
                    String number = cursor.getString(1);
                    content = name + "\n" + number;
                }
                cursor.close();
            }
        }
        db.close();
        return content;
    }
}
