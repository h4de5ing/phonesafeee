package mo.com.phonesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 程序锁数据库
 *
 * Created by Gh0st on 2015/9/13 09:23
 */


public class AppLockDBHelper extends SQLiteOpenHelper{
    public AppLockDBHelper(Context context) {
        super(context, AppLockDB.APPLOCK_DATABASE_NAME, null, AppLockDB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppLockDB.ApplockTable.TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
