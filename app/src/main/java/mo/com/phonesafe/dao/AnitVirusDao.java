package mo.com.phonesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * 作者：MoMxMo on 2015/9/15 16:48
 * 邮箱：xxxx@qq.com
 *
 * 病毒库查询
 */


public class AnitVirusDao {
    private Context mContext;

    public AnitVirusDao(Context context) {
        mContext = context;
    }

    public static boolean insert(Context context,String md5) {
        File file = new File(context.getFilesDir(),"antivirus.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);

        long insert = 0;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("md5", md5);
            values.put("type", 6);
            values.put("name", "Android.Adware.AirAD.a");
            values.put("desc","恶意后台扣费,病毒木马程序");
            insert = db.insert("datable", null, values);
        }
        db.close();
        return insert>0;
    }

    /**
     * 根据md5判断程序是否是病毒
     * @param context
     * @param md5
     * @return
     */
    public static boolean isVirus(Context context, String md5) {
        File file = new File(context.getFilesDir(),"antivirus.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

        int count = 0;
        if (db.isOpen()) {
            String sql = "select count(1) from datable where md5=?";
            Cursor cursor = db.rawQuery(sql, new String[]{md5});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
            }
        }
        db.close();
        return count>0;
    }
}
